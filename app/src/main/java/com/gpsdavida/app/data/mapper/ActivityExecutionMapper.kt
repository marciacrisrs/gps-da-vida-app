package com.gpsdavida.app.data.mapper

import com.gpsdavida.app.data.local.ActivityExecutionEntity
import com.gpsdavida.app.domain.model.ActivityExecution
import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.TimeRange
import java.time.Instant

fun ActivityInstance.toExecutionEntity(): ActivityExecutionEntity = ActivityExecutionEntity(
    activityInstanceId = id.value,
    status = status.name,
    plannedStart = planned.start.toString(),
    plannedEnd = planned.end.toString(),
    actualStart = actual?.start?.toString(),
    actualEnd = actual?.end?.toString(),
)

fun ActivityExecutionEntity.toDomain(): ActivityExecution = ActivityExecution(
    activityInstanceId = ActivityInstanceId(activityInstanceId),
    status = ActivityStatus.valueOf(status),
    planned = TimeRange(Instant.parse(plannedStart), Instant.parse(plannedEnd)),
    actual = if (actualStart != null && actualEnd != null) {
        TimeRange(Instant.parse(actualStart), Instant.parse(actualEnd))
    } else {
        null
    },
)
