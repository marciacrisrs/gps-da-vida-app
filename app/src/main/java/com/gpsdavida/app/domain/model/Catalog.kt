package com.gpsdavida.app.domain.model

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class Event(
    val id: EventId,
    val title: String,
    val range: TimeRange,
    val recurrenceDays: Set<DayOfWeek> = emptySet(),
    val priority: Priority = Priority.REQUIRED,
    val energy: Energy? = null,
    val goalId: GoalId? = null,
) {
    val flexibility: Flexibility get() = Flexibility.FIXED

    fun occursOn(date: LocalDate, zone: ZoneId): Boolean {
        val startDate = range.start.atZone(zone).toLocalDate()
        if (recurrenceDays.isEmpty()) return date == startDate
        return !date.isBefore(startDate) && date.dayOfWeek in recurrenceDays
    }
}

data class Task(
    val id: TaskId,
    val title: String,
    val plannedDuration: Duration,
    val priority: Priority,
    val due: Instant? = null,
    val energy: Energy? = null,
    val goalId: GoalId? = null,
) {
    val flexibility: Flexibility get() = Flexibility.FLEXIBLE
}

data class Habit(
    val id: HabitId,
    val title: String,
    val plannedDuration: Duration,
    val daysOfWeek: Set<DayOfWeek>,
    val window: LocalTimeWindow? = null,
    val priority: Priority = Priority.IMPORTANT,
    val energy: Energy? = null,
    val goalId: GoalId? = null,
) {
    val flexibility: Flexibility get() = Flexibility.FLEXIBLE
}

data class RoutineStep(
    val id: RoutineStepId,
    val title: String,
    val plannedDuration: Duration,
    val order: Int,
)

data class Routine(
    val id: RoutineId,
    val title: String,
    val steps: List<RoutineStep>,
    val startTime: LocalTime? = null,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val priority: Priority = Priority.IMPORTANT,
    val energy: Energy? = null,
    val goalId: GoalId? = null,
) {
    val flexibility: Flexibility get() = Flexibility.FLEXIBLE
}

data class Availability(
    val id: AvailabilityId,
    val dayOfWeek: DayOfWeek,
    val window: LocalTimeWindow,
    val kind: AvailabilityKind,
)

data class Goal(
    val id: GoalId,
    val title: String,
)

data class Dependency(
    val id: DependencyId,
    val predecessor: ActivitySource,
    val successor: ActivitySource,
)
