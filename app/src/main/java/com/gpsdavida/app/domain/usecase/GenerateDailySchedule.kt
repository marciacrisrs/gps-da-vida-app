package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivitySource
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
                compareByDescending<ActivityInstance> { dependencyDepth(it.source, dependencies) }
                    .thenByDescending { it.priority.weight }
                    .thenBy { it.planned.start },
            )

        for (activity in flexible) {
            val predecessors = dependencies
                .filter { it.successor == activity.source }
                .mapNotNull { dependency -> scheduled.firstOrNull { it.source == dependency.predecessor } }

            if (predecessors.size != dependencies.count { it.successor == activity.source }) {
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
                earliestStart = predecessors.maxOfOrNull {
                    it.planned.end.plus(bufferAfter(it, defaultBuffer)).plus(travelDuration(it, activity, travelTimes))
                },
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
        earliestStart: Instant? = null,
    ): TimeRange? {
        val windows = availableWindows(date, availability)
        val occupied = scheduled.sortedBy { it.planned.start }
        val duration = activity.plannedDuration

        for (window in windows) {
            var cursor = maxOf(window.start.atDate(date, zoneId), earliestStart ?: window.start.atDate(date, zoneId))
            val windowEnd = window.end.atDate(date, zoneId)
            var previous: ActivityInstance? = null

            for (existing in occupied) {
                if (existing.planned.end <= cursor) {
                    previous = existing
                    continue
                }
                if (existing.planned.start >= windowEnd) break

                val candidate = cursorAfter(previous, cursor, activity, defaultBuffer, travelTimes)
                if (candidate.plus(duration) <= existing.planned.start && candidate.plus(duration) <= windowEnd) {
                    return TimeRange(candidate, candidate.plus(duration))
                }

                cursor = maxOf(cursor, existing.planned.end.plus(bufferAfter(existing, defaultBuffer)))
                previous = existing
            }

            val candidate = cursorAfter(previous, cursor, activity, defaultBuffer, travelTimes)
            if (candidate.plus(duration) <= windowEnd) {
                return TimeRange(candidate, candidate.plus(duration))
            }
        }
        return null
    }

    private fun cursorAfter(
        previous: ActivityInstance?,
        cursor: Instant,
        target: ActivityInstance,
        defaultBuffer: Duration,
        travelTimes: List<TravelTime>,
    ): Instant {
        if (previous == null) return cursor
        val afterPrevious = previous.planned.end
            .plus(bufferAfter(previous, defaultBuffer))
            .plus(travelDuration(previous, target, travelTimes))
        return maxOf(cursor, afterPrevious)
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

    private fun dependencyDepth(
        source: ActivitySource,
        dependencies: List<Dependency>,
        visiting: Set<ActivitySource> = emptySet(),
    ): Int {
        if (source in visiting) return 0
        val predecessors = dependencies.filter { it.successor == source }.map { it.predecessor }
        if (predecessors.isEmpty()) return 0
        return 1 + predecessors.maxOf { dependencyDepth(it, dependencies, visiting + source) }
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
