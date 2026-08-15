package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.port.TaskRepository
import java.time.Clock
import javax.inject.Inject

class CompleteTask @Inject constructor(
    private val tasks: TaskRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(id: TaskId, done: Boolean = true) {
        val current = tasks.getById(id) ?: return
        tasks.save(current.copy(completedAt = if (done) clock.instant() else null))
    }
}
