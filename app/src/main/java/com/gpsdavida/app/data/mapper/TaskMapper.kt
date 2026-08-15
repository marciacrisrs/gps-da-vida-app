package com.gpsdavida.app.data.mapper

import com.gpsdavida.app.data.local.TaskEntity
import com.gpsdavida.app.domain.model.Priority
import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.model.TaskId
import java.time.Duration
import java.time.Instant

fun TaskEntity.toDomain(): Task =
    Task(
        id = TaskId(id),
        title = title,
        plannedDuration = Duration.ofMinutes(plannedDurationMinutes),
        priority = Priority.valueOf(priority),
        due = dueEpochMilli?.let(Instant::ofEpochMilli),
        completedAt = completedAtEpochMilli?.let(Instant::ofEpochMilli),
    )

fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id.value,
        title = title,
        plannedDurationMinutes = plannedDuration.toMinutes().coerceAtLeast(1),
        priority = priority.name,
        dueEpochMilli = due?.toEpochMilli(),
        completedAtEpochMilli = completedAt?.toEpochMilli(),
    )
