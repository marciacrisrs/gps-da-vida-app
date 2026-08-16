package com.gpsdavida.app.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

/** A weekly recurring availability rule. FREE windows are usable time; BLOCKED windows are exclusions. */
data class Availability(
    val id: AvailabilityId,
    val dayOfWeek: DayOfWeek,
    val window: LocalTimeWindow,
    val kind: AvailabilityKind,
)