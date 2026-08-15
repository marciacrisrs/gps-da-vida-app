package com.gpsdavida.app.domain.model

enum class Priority {
    REQUIRED,
    IMPORTANT,
    DESIRABLE,
    LEISURE,
}

enum class Energy {
    LOW,
    MEDIUM,
    HIGH,
}

enum class Flexibility {
    FIXED,
    FLEXIBLE,
}

enum class ActivityStatus {
    PENDING,
    DONE,
    SKIPPED,
    DEFERRED,
}

enum class AvailabilityKind {
    FREE,
    BLOCKED,
}
