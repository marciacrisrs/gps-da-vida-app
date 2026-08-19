package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import javax.inject.Inject

class DeferActivityInstance @Inject constructor(
    private val record: RecordActivityExecution,
) {
    suspend operator fun invoke(activity: ActivityInstance) {
        record.defer(activity)
    }
}
