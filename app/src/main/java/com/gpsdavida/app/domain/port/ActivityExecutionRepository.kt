package com.gpsdavida.app.domain.port

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId

interface ActivityExecutionRepository {
    suspend fun save(activity: ActivityInstance)

    suspend fun getById(id: ActivityInstanceId): ActivityInstance?
}
