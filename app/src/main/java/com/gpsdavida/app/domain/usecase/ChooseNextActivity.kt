package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityKind
import com.gpsdavida.app.domain.model.Dependency
import com.gpsdavida.app.domain.model.Flexibility
import com.gpsdavida.app.domain.model.NextActionContext
import com.gpsdavida.app.domain.model.NextActionDecision
import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

/**
 * Chooses the best executable activity without knowing where it came from.
 *
 * The use case deliberately stops at decision-making. Replanning and automatic
 * rescheduling belong to later slices of the GPS engine.
 */
class ChooseNextActivity @Inject constructor() {
    /** Backwards-compatible entry point for callers that only need a recommendation. */
    operator fun invoke(
        activities: List<ActivityInstance>,
        now: Instant,
    ): ActivityInstance? = invoke(
        activities = activities,
        context = NextActionContext(now = now),
    ).recommended

    operator fun invoke(
        activities: List<ActivityInstance>,
        context: NextActionContext,
    ): NextActionDecision {
        val executable = activities
            .asSequence()
            .filter { it.status == ActivityStatus.PENDING }
            .filter { isAvailable(it, context) }
            .filter { dependenciesSatisfied(it, activities, context.dependencies) }
            .toList()

        val current = executable
            .filter { it.planned.start <= context.now && context.now < it.planned.end }
            .minWithOrNull(currentComparator)

        val next = executable
            .asSequence()
            .filter { it.id != current?.id }
            .sortedWith(nextComparator(context.now))
            .firstOrNull()

        return NextActionDecision(
            current = current,
            next = next,
        )
    }

    private fun isAvailable(
        activity: ActivityInstance,
        context: NextActionContext,
    ): Boolean {
        if (context.availability.isEmpty()) return true

        val start = activity.planned.start.atZone(context.zoneId)
        val end = activity.planned.end.atZone(context.zoneId)
        if (start.toLocalDate() != end.toLocalDate()) return false

        val dayRules = context.availability.filter { it.dayOfWeek == start.dayOfWeek }
        if (dayRules.isEmpty()) return true

        val blocked = dayRules
            .filter { it.kind == AvailabilityKind.BLOCKED }
            .any { rangesOverlap(start.toLocalTime(), end.toLocalTime(), it.window.start, it.window.end) }
        if (blocked) return false

        val freeWindows = dayRules
            .filter { it.kind == AvailabilityKind.FREE }
            .map { it.window }

        return freeWindows.isEmpty() || freeWindows.any {
            it.start <= start.toLocalTime() && end.toLocalTime() <= it.end
        }
    }

    private fun dependenciesSatisfied(
        activity: ActivityInstance,
        activities: List<ActivityInstance>,
        dependencies: List<Dependency>,
    ): Boolean = dependencies
        .filter { it.successor == activity.source }
        .all { dependency ->
            activities.any {
                it.source == dependency.predecessor && it.status == ActivityStatus.DONE
            }
        }

    private fun rangesOverlap(
        start: LocalTime,
        end: LocalTime,
        otherStart: LocalTime,
        otherEnd: LocalTime,
    ): Boolean = start < otherEnd && end > otherStart

    private val currentComparator = compareBy<ActivityInstance> { it.priority.weight }
        .thenBy { it.planned.start }

    private fun nextComparator(now: Instant) = compareBy<ActivityInstance> { urgency(it, now) }
        .thenBy { it.priority.weight }
        .thenBy { it.flexibility != Flexibility.FIXED }
        .thenBy { it.planned.start }

    private fun urgency(activity: ActivityInstance, now: Instant): Int =
        if (activity.planned.start <= now) 0 else 1
}
