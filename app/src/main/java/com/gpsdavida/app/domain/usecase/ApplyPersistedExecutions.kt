package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityExecution
import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import javax.inject.Inject

/** Overlays persisted execution state onto freshly materialized activities. */
class ApplyPersistedExecutions @Inject constructor() {
    operator fun invoke(
        activities: List<ActivityInstance>,
        persisted: Map<ActivityInstanceId, ActivityExecution>,
    ): List<ActivityInstance> = activities.map { activity ->
        val execution = persisted[activity.id] ?: return@map activity
        activity.copy(
            status = execution.status,
            actual = execution.actual,
        )
    }
}
