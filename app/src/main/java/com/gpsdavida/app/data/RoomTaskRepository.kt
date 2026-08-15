package com.gpsdavida.app.data

import com.gpsdavida.app.data.local.TaskDao
import com.gpsdavida.app.data.mapper.toDomain
import com.gpsdavida.app.data.mapper.toEntity
import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.port.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository @Inject constructor(
    private val dao: TaskDao,
) : TaskRepository {
    override fun observeAll(): Flow<List<Task>> = dao.observeAll().map { rows ->
        rows.map { it.toDomain() }
    }

    override suspend fun getById(id: TaskId): Task? = dao.getById(id.value)?.toDomain()

    override suspend fun save(task: Task) {
        dao.upsert(task.toEntity())
    }

    override suspend fun delete(id: TaskId) {
        dao.delete(id.value)
    }
}
