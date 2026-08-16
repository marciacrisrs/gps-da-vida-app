package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.TimeRange
import java.time.Instant
import javax.inject.Inject

/** Applies an explicit execution transition to a materialized activity instance. */
class RecordActivityExecution @Inject constructor() {
    fun complete(
        activity: ActivityInstance,
        actualStart: Instant,
        actualEnd: Instant,
    ): ActivityInstance = activity.completed(TimeRange(actualStart, actualEnd))

    fun skip(activity: ActivityInstance): ActivityInstance = activity.skipped()

    fun defer(activity: ActivityInstance): ActivityInstance = activity.deferred()
}
