package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.port.TaskRepository
import javax.inject.Inject

class SaveTask @Inject constructor(
    private val tasks: TaskRepository,
) {
    suspend operator fun invoke(task: Task) {
        require(task.title.isNotBlank()) { "title" }
        require(!task.plannedDuration.isNegative && !task.plannedDuration.isZero) { "duration" }
        tasks.save(task)
    }
}
