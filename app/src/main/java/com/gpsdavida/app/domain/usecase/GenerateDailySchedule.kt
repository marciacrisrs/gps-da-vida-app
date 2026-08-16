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

/** Builds an executable daily schedule from materialized activity instances. */
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
        val conflicts = mutableListOf<ScheduleConflict>()

        fixed.zipWithNext().forEach { (left, right) ->
            if (left.planned.end.plus(bufferAfter(left, defaultBuffer)) > right.planned.start) {
                conflicts += ScheduleConflict(right, ScheduleConflictReason.FIXED_OVERLAP)
            }
        }

        val scheduled = fixed.toMutableList()
        val pending = eligible.filter { it.flexibility != Flexibility.FIXED }.toMutableList()

        while (pending.isNotEmpty()) {
            val ready = pending.filter { activity ->
                dependencies
                    .filter { it.successor == activity.source }
                    .all { dependency -> scheduled.any { it.source == dependency.predecessor } }
            }

            if (ready.isEmpty()) {
                pending.forEach { conflicts += ScheduleConflict(it, ScheduleConflictReason.DEPENDENCY_NOT_SATISFIED) }
                break
            }

            val activity = ready.minWith(
                compareBy<ActivityInstance> { dependencyDepth(it.source, dependencies) }
                    .thenBy { it.priority.weight }
                    .thenBy { it.planned.start },
            )
            pending.remove(activity)

            val predecessors = dependencies
                .filter { it.successor == activity.source }
                .mapNotNull { dependency -> scheduled.firstOrNull { it.source == dependency.predecessor } }

            val earliestStart = maxOf(
                activity.planned.start,
                predecessors.maxOfOrNull {
                    it.planned.end
                        .plus(bufferAfter(it, defaultBuffer))
                        .plus(travelDuration(it, activity, travelTimes))
                } ?: activity.planned.start,
            )

            val slot = findSlot(
                activity = activity,
                scheduled = scheduled,
                availability = availability,
                date = date,
                defaultBuffer = defaultBuffer,
                travelTimes = travelTimes,
                zoneId = zoneId,
                earliestStart = earliestStart,
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
        earliestStart: Instant,
    ): TimeRange? {
        val windows = availableWindows(date, availability)
        val occupied = scheduled.sortedBy { it.planned.start }
        val duration = activity.plannedDuration

        for (window in windows) {
            val windowStart = window.start.atDate(date, zoneId)
            val windowEnd = window.end.atDate(date, zoneId)
            var cursor = maxOf(windowStart, earliestStart)
            if (cursor >= windowEnd) continue

            for (existing in occupied) {
                if (existing.planned.end <= cursor) continue
                if (existing.planned.start >= windowEnd) break

                if (cursor.plus(duration) <= existing.planned.start && cursor.plus(duration) <= windowEnd) {
                    return TimeRange(cursor, cursor.plus(duration))
                }

                cursor = maxOf(
                    cursor,
                    existing.planned.end
                        .plus(bufferAfter(existing, defaultBuffer))
                        .plus(travelDuration(existing, activity, travelTimes)),
                )
                if (cursor >= windowEnd) break
            }

            if (cursor.plus(duration) <= windowEnd) {
                return TimeRange(cursor, cursor.plus(duration))
            }
        }
        return null
    }

    private fun availableWindows(
        date: LocalDate,
        availability: List<Availability>,
    ): List<LocalTimeWindow> {
        if (availability.isEmpty()) {
            return listOf(LocalTimeWindow(LocalTime.MIN, LocalTime.MAX))
        }

        val rules = availability.filter { it.dayOfWeek == date.dayOfWeek }
        val blocked = rules
            .filter { it.kind == AvailabilityKind.BLOCKED }
            .map { it.window }
            .sortedBy { it.start }

        val free = rules
            .filter { it.kind == AvailabilityKind.FREE }
            .map { it.window }
            .sortedBy { it.start }

        val base = if (free.isEmpty()) {
            listOf(LocalTimeWindow(LocalTime.MIN, LocalTime.MAX))
        } else {
            free
        }

        return base.flatMap { window -> subtractBlocked(window, blocked) }
    }

    private fun subtractBlocked(
        window: LocalTimeWindow,
        blocked: List<LocalTimeWindow>,
    ): List<LocalTimeWindow> {
        val result = mutableListOf<LocalTimeWindow>()
        var cursor = window.start

        for (block in blocked) {
            if (block.end <= cursor || block.start >= window.end) continue
            val blockStart = maxOf(block.start, window.start)
            val blockEnd = minOf(block.end, window.end)
            if (cursor < blockStart) result += LocalTimeWindow(cursor, blockStart)
            cursor = maxOf(cursor, blockEnd)
            if (cursor >= window.end) break
        }

        if (cursor < window.end) result += LocalTimeWindow(cursor, window.end)
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
