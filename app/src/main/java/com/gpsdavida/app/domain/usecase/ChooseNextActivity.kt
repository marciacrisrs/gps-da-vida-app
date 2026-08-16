package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.Flexibility
import java.time.Instant
import javax.inject.Inject

/**
 * Selects the next executable activity without knowing where the activity came from.
 *
 * Policy for this first planning slice:
 * 1. Ignore activities already finished, skipped or deferred.
 * 2. Keep an activity currently in progress ahead of everything else.
 * 3. Prefer overdue activities over future activities.
 * 4. Within the same urgency, prefer higher priority.
 * 5. Prefer fixed activities before flexible ones when all previous rules tie.
 * 6. Preserve chronological order as the final tie-breaker.
 */
class ChooseNextActivity @Inject constructor() {
    operator fun invoke(
        activities: List<ActivityInstance>,
        now: Instant,
    ): ActivityInstance? {
        val pending = activities.filter { it.status == ActivityStatus.PENDING }
        if (pending.isEmpty()) return null

        val current = pending
            .filter { it.planned.start <= now && now < it.planned.end }
            .minWithOrNull(compareBy<ActivityInstance> { it.priority.weight }.thenBy { it.planned.start })
        if (current != null) return current

        return pending
            .sortedWith(
                compareBy<ActivityInstance> { urgency(it, now) }
                    .thenBy { it.priority.weight }
                    .thenBy { it.flexibility != Flexibility.FIXED }
                    .thenBy { it.planned.start },
            )
            .firstOrNull()
    }

    private fun urgency(activity: ActivityInstance, now: Instant): Int =
        if (activity.planned.start <= now) 0 else 1
}
