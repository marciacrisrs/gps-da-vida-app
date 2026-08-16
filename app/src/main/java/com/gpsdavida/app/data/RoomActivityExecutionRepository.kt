package com.gpsdavida.app.data

import com.gpsdavida.app.data.local.ActivityExecutionDao
import com.gpsdavida.app.data.mapper.toDomain
import com.gpsdavida.app.data.mapper.toExecutionEntity
import com.gpsdavida.app.domain.model.ActivityExecution
import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import com.gpsdavida.app.domain.port.ActivityExecutionRepository
import javax.inject.Inject

class RoomActivityExecutionRepository @Inject constructor(
    private val dao: ActivityExecutionDao,
) : ActivityExecutionRepository {
    override suspend fun save(activity: ActivityInstance) {
        dao.upsert(activity.toExecutionEntity())
    }

    override suspend fun getById(id: ActivityInstanceId): ActivityExecution? =
        dao.getById(id.value)?.toDomain()
}
