package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.port.TaskRepository
import javax.inject.Inject

class GetTask @Inject constructor(
    private val tasks: TaskRepository,
) {
    suspend operator fun invoke(id: TaskId): Task? = tasks.getById(id)
}
