package com.gpsdavida.app.domain.port

import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.model.TaskId
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun getById(id: TaskId): Task?
    suspend fun save(task: Task)
    suspend fun delete(id: TaskId)
}
