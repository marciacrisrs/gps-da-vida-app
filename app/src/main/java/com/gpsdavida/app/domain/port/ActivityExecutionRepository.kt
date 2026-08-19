package com.gpsdavida.app.domain.port

import com.gpsdavida.app.domain.model.ActivityExecution
import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import kotlinx.coroutines.flow.Flow

interface ActivityExecutionRepository {
    suspend fun save(activity: ActivityInstance)

    suspend fun getById(id: ActivityInstanceId): ActivityExecution?

    fun observeAll(): Flow<List<ActivityExecution>>
}
