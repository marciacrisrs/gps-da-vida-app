package com.gpsdavida.app.domain.model

import java.time.Duration
import java.time.Instant
import java.time.ZoneId

data class NextActionDecision(
    val current: ActivityInstance?,
    val next: ActivityInstance?,
    val travelDurationToNext: Duration = Duration.ZERO,
) {
    val recommended: ActivityInstance?
        get() = current ?: next
}

data class NextActionContext(
    val now: Instant,
    val availability: List<Availability> = emptyList(),
    val dependencies: List<Dependency> = emptyList(),
    val currentEnergy: Energy? = null,
    val currentContext: ExecutionContext? = null,
    val currentLocation: LocationId? = null,
    val travelTimes: List<TravelTime> = emptyList(),
    val defaultBuffer: Duration = Duration.ZERO,
    val zoneId: ZoneId = ZoneId.systemDefault(),
)
