package com.gpsdavida.app.data.mapper

import com.gpsdavida.app.data.local.AvailabilityEntity
import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityId
import com.gpsdavida.app.domain.model.AvailabilityKind
import com.gpsdavida.app.domain.model.LocalTimeWindow
import java.time.DayOfWeek
import java.time.LocalTime

fun AvailabilityEntity.toDomain(): Availability = Availability(
    id = AvailabilityId(id),
    dayOfWeek = DayOfWeek.of(dayOfWeek),
    window = LocalTimeWindow(
        start = LocalTime.of(startMinute / 60, startMinute % 60),
        end = LocalTime.of(endMinute / 60, endMinute % 60),
    ),
    kind = AvailabilityKind.valueOf(kind),
)

fun Availability.toEntity(): AvailabilityEntity = AvailabilityEntity(
    id = id.value,
    dayOfWeek = dayOfWeek.value,
    startMinute = window.start.hour * 60 + window.start.minute,
    endMinute = window.end.hour * 60 + window.end.minute,
    kind = kind.name,
)