package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.port.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveTasks @Inject constructor(
    private val tasks: TaskRepository,
) {
    operator fun invoke(): Flow<List<Task>> =
        tasks.observeAll().map { list ->
            list.sortedWith(compareBy<Task> { it.isDone }.thenBy { it.due }.thenBy { it.title })
        }
}
