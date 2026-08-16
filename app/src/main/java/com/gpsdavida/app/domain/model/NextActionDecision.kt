package com.gpsdavida.app.domain.model

import java.time.Instant
import java.time.ZoneId

data class NextActionDecision(
    val current: ActivityInstance?,
    val next: ActivityInstance?,
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
    val zoneId: ZoneId = ZoneId.systemDefault(),
)
