package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityKind
import com.gpsdavida.app.domain.model.DailySchedule
import com.gpsdavida.app.domain.model.Dependency
import com.gpsdavida.app.domain.model.Flexibility
import com.gpsdavida.app.domain.model.LocalTimeWindow
import com.gpsdavida.app.domain.model.ScheduleConflict
import com.gpsdavida.app.domain.model.ScheduleConflictReason
import com.gpsdavida.app.domain.model.TimeRange
import com.gpsdavida.app.domain.model.TravelTime
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Builds one executable daily schedule from already materialized activity instances.
 *
 * The generator is intentionally agnostic to activity origin. Recurrences, plans,
 * habits and other domains are expected to materialize their occurrences before
 * reaching this boundary.
 */
class GenerateDailySchedule @Inject constructor() {
    operator fun invoke(
        activities: List<ActivityInstance>,
        date: LocalDate,
        availability: List<Availability> = emptyList(),
        dependencies: List<Dependency> = emptyList(),
        defaultBuffer: Duration = Duration.ZERO,
        travelTimes: List<TravelTime> = emptyList(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): DailySchedule {
        val eligible = activities
            .filter { it.status == ActivityStatus.PENDING }
            .filter { it.planned.start.atZone(zoneId).toLocalDate() == date }

        val fixed = eligible
            .filter { it.flexibility == Flexibility.FIXED }
            .sortedBy { it.planned.start }
            .toMutableList()
        val conflicts = mutableListOf<ScheduleConflict>()

        fixed.zipWithNext().forEach { (left, right) ->
            if (left.planned.end.plus(bufferAfter(left, defaultBuffer)) > right.planned.start) {
                conflicts += ScheduleConflict(right, ScheduleConflictReason.FIXED_OVERLAP)
            }
        }

        val scheduled = fixed.toMutableList()
        val flexible = eligible
            .filter { it.flexibility != Flexibility.FIXED }
            .sortedWith(
                compareByDescending<ActivityInstance> { it.priority.weight }
                    .thenBy { it.planned.start },
            )

        for (activity in flexible) {
            if (!dependenciesSatisfied(activity, scheduled, dependencies)) {
                conflicts += ScheduleConflict(activity, ScheduleConflictReason.DEPENDENCY_NOT_SATISFIED)
                continue
            }

            val slot = findSlot(
                activity = activity,
                scheduled = scheduled,
                availability = availability,
                date = date,
                defaultBuffer = defaultBuffer,
                travelTimes = travelTimes,
                zoneId = zoneId,
            )

            if (slot == null) {
                conflicts += ScheduleConflict(activity, ScheduleConflictReason.NO_AVAILABLE_WINDOW)
            } else {
                scheduled += activity.copy(planned = slot)
            }
        }

        return DailySchedule(
            activities = scheduled.sortedBy { it.planned.start },
            conflicts = conflicts,
        )
    }

    private fun findSlot(
        activity: ActivityInstance,
        scheduled: List<ActivityInstance>,
        availability: List<Availability>,
        date: LocalDate,
        defaultBuffer: Duration,
        travelTimes: List<TravelTime>,
        zoneId: ZoneId,
    ): TimeRange? {
        val windows = availableWindows(date, availability)
        val occupied = scheduled.sortedBy { it.planned.start }
        val duration = activity.plannedDuration

        for (window in windows) {
            var cursor = window.start.atDate(date, zoneId)
            val windowEnd = window.end.atDate(date, zoneId)

            for (existing in occupied) {
                if (existing.planned.end <= cursor) continue
                if (existing.planned.start >= windowEnd) break

                val candidate = fitAfter(cursor, existing, activity, defaultBuffer, travelTimes)
                if (candidate.plus(duration) <= existing.planned.start && candidate.plus(duration) <= windowEnd) {
                    return TimeRange(candidate, candidate.plus(duration))
                }
                cursor = maxOf(cursor, existing.planned.end.plus(bufferAfter(existing, defaultBuffer)))
            }

            val candidate = cursor
            if (candidate.plus(duration) <= windowEnd) {
                return TimeRange(candidate, candidate.plus(duration))
            }
        }
        return null
    }

    private fun fitAfter(
        cursor: Instant,
        existing: ActivityInstance,
        target: ActivityInstance,
        defaultBuffer: Duration,
        travelTimes: List<TravelTime>,
    ): Instant {
        if (existing.planned.end <= cursor) return cursor
        val travel = travelDuration(existing, target, travelTimes)
        return maxOf(cursor, existing.planned.end.plus(bufferAfter(existing, defaultBuffer)).plus(travel))
    }

    private fun availableWindows(
        date: LocalDate,
        availability: List<Availability>,
    ): List<LocalTimeWindow> {
        if (availability.isEmpty()) {
            return listOf(LocalTimeWindow(LocalTime.MIN, LocalTime.MAX))
        }

        val rules = availability.filter { it.dayOfWeek == date.dayOfWeek }
        val free = rules.filter { it.kind == AvailabilityKind.FREE }.map { it.window }
        if (free.isNotEmpty()) return free.sortedBy { it.start }

        val blocked = rules.filter { it.kind == AvailabilityKind.BLOCKED }.map { it.window }
        if (blocked.isEmpty()) return listOf(LocalTimeWindow(LocalTime.MIN, LocalTime.MAX))

        val result = mutableListOf<LocalTimeWindow>()
        var cursor = LocalTime.MIN
        for (block in blocked.sortedBy { it.start }) {
            if (cursor < block.start) result += LocalTimeWindow(cursor, block.start)
            cursor = maxOf(cursor, block.end)
        }
        if (cursor < LocalTime.MAX) result += LocalTimeWindow(cursor, LocalTime.MAX)
        return result
    }

    private fun dependenciesSatisfied(
        activity: ActivityInstance,
        scheduled: List<ActivityInstance>,
        dependencies: List<Dependency>,
    ): Boolean = dependencies
        .filter { it.successor == activity.source }
        .all { dependency ->
            scheduled.any { it.source == dependency.predecessor && it.status == ActivityStatus.DONE }
        }

    private fun bufferAfter(activity: ActivityInstance, defaultBuffer: Duration): Duration =
        activity.bufferAfter ?: defaultBuffer

    private fun travelDuration(
        from: ActivityInstance,
        to: ActivityInstance,
        travelTimes: List<TravelTime>,
    ): Duration {
        val fromLocation = from.location?.id ?: return Duration.ZERO
        val toLocation = to.location?.id ?: return Duration.ZERO
        if (fromLocation == toLocation) return Duration.ZERO
        return travelTimes.firstOrNull { it.from == fromLocation && it.to == toLocation }?.duration
            ?: Duration.ZERO
    }

    private fun LocalTime.atDate(date: LocalDate, zoneId: ZoneId): Instant =
        date.atTime(this).atZone(zoneId).toInstant()
}
