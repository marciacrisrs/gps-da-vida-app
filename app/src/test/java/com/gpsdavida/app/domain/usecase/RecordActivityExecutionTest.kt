package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import com.gpsdavida.app.domain.model.ActivitySource
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.Flexibility
import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.model.TimeRange
import java.time.Duration
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecordActivityExecutionTest {
    private val plannedStart = Instant.parse("2026-01-01T09:00:00Z")
    private val plannedEnd = Instant.parse("2026-01-01T10:00:00Z")
    private val useCase = RecordActivityExecution()

    @Test
    fun `complete records actual time and duration`() {
        val actualEnd = Instant.parse("2026-01-01T10:30:00Z")

        val result = useCase.complete(activity(), plannedStart, actualEnd)

        assertEquals(ActivityStatus.DONE, result.status)
        assertEquals(TimeRange(plannedStart, actualEnd), result.actual)
        assertEquals(Duration.ofMinutes(90), result.actualDuration)
        assertEquals(Duration.ofMinutes(30), result.durationVariance)
    }

    @Test
    fun `skip changes status without inventing execution time`() {
        val result = useCase.skip(activity())

        assertEquals(ActivityStatus.SKIPPED, result.status)
        assertNull(result.actual)
        assertNull(result.actualDuration)
    }

    @Test
    fun `defer changes status without inventing execution time`() {
        val result = useCase.defer(activity())

        assertEquals(ActivityStatus.DEFERRED, result.status)
        assertNull(result.actual)
        assertNull(result.actualDuration)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `cannot complete an activity that is not pending`() {
        useCase.complete(activity().skipped(), plannedStart, plannedEnd)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `cannot skip an activity that is not pending`() {
        useCase.skip(activity().completed(TimeRange(plannedStart, plannedEnd)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `cannot defer an activity that is not pending`() {
        useCase.defer(activity().completed(TimeRange(plannedStart, plannedEnd)))
    }

    private fun activity() = ActivityInstance(
        id = ActivityInstanceId("activity-1"),
        source = ActivitySource.FromTask(TaskId("task-1")),
        flexibility = Flexibility.FLEXIBLE,
        planned = TimeRange(plannedStart, plannedEnd),
    )
}
