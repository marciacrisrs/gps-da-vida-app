package com.gpsdavida.app.data.mapper

import com.gpsdavida.app.data.local.EventEntity
import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.EventId
import com.gpsdavida.app.domain.model.Priority
import com.gpsdavida.app.domain.model.TimeRange
import java.time.DayOfWeek
import java.time.Instant

fun EventEntity.toDomain(): Event =
    Event(
        id = EventId(id),
        title = title,
        range = TimeRange(
            start = Instant.ofEpochMilli(startEpochMilli),
            end = Instant.ofEpochMilli(endEpochMilli),
        ),
        recurrenceDays = recurrenceDays.toDaySet(),
        priority = Priority.valueOf(priority),
    )

fun Event.toEntity(): EventEntity =
    EventEntity(
        id = id.value,
        title = title,
        startEpochMilli = range.start.toEpochMilli(),
        endEpochMilli = range.end.toEpochMilli(),
        recurrenceDays = recurrenceDays.joinToString(",") { it.name },
        priority = priority.name,
    )

private fun String.toDaySet(): Set<DayOfWeek> =
    if (isBlank()) emptySet()
    else split(',').map { DayOfWeek.valueOf(it) }.toSet()
