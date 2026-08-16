package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.ActivityInstance
import com.gpsdavida.app.domain.model.ActivityInstanceId
import com.gpsdavida.app.domain.model.ActivitySource
import com.gpsdavida.app.domain.model.ActivityStatus
import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityId
import com.gpsdavida.app.domain.model.AvailabilityKind
import com.gpsdavida.app.domain.model.Dependency
import com.gpsdavida.app.domain.model.DependencyId
import com.gpsdavida.app.domain.model.Energy
import com.gpsdavida.app.domain.model.Flexibility
import com.gpsdavida.app.domain.model.LocalTimeWindow
import com.gpsdavida.app.domain.model.NextActionContext
import com.gpsdavida.app.domain.model.Priority
import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.model.TimeRange
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ChooseNextActivityTest {
    private val useCase = ChooseNextActivity()
    private val now = Instant.parse("2026-08-17T10:00:00Z")

    @Test
    fun `current activity is recommended before overdue activity`() {
        val overdue = activity("overdue", "08:00:00", "08:30:00", Priority.REQUIRED)
        val current = activity("current", "09:30:00", "10:30:00", Priority.IMPORTANT)

        val decision = useCase(listOf(overdue, current), NextActionContext(now))

        assertEquals(current.id, decision.current?.id)
        assertEquals(overdue.id, decision.next?.id)
        assertEquals(current.id, decision.recommended?.id)
    }

    @Test
    fun `priority breaks ties between overdue activities`() {
        val important = activity("important", "09:00:00", "09:30:00", Priority.IMPORTANT)
        val required = activity("required", "09:30:00", "09:45:00", Priority.REQUIRED)

        val decision = useCase(listOf(important, required), NextActionContext(now))

        assertEquals(required.id, decision.recommended?.id)
    }

    @Test
    fun `blocked availability makes an activity non executable`() {
        val blocked = activity("blocked", "10:30:00", "11:00:00", Priority.REQUIRED)
        val free = activity("free", "10:00:00", "10:30:00", Priority.IMPORTANT)
        val availability = listOf(
            Availability(
                id = AvailabilityId("a1"),
                dayOfWeek = DayOfWeek.MONDAY,
                window = LocalTimeWindow(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                kind = AvailabilityKind.FREE,
            ),
            Availability(
                id = AvailabilityId("a2"),
                dayOfWeek = DayOfWeek.MONDAY,
                window = LocalTimeWindow(LocalTime.of(10, 30), LocalTime.of(11, 0)),
                kind = AvailabilityKind.BLOCKED,
            ),
        )

        val decision = useCase(
            listOf(blocked, free),
            NextActionContext(now = now, availability = availability),
        )

        assertEquals(free.id, decision.recommended?.id)
        assertEquals(free.id, decision.current?.id)
    }

    @Test
    fun `dependency blocks successor until predecessor is done`() {
        val predecessor = activity("predecessor", "08:00:00", "08:30:00", Priority.REQUIRED)
        val successor = activity("successor", "10:30:00", "11:00:00", Priority.REQUIRED)
        val dependency = Dependency(
            id = DependencyId("d1"),
            predecessor = predecessor.source,
            successor = successor.source,
        )

        val blockedDecision = useCase(
            listOf(predecessor, successor),
            NextActionContext(now = now, dependencies = listOf(dependency)),
        )
        assertEquals(predecessor.id, blockedDecision.recommended?.id)

        val completed = predecessor.copy(status = ActivityStatus.DONE)
        val unblockedDecision = useCase(
            listOf(completed, successor),
            NextActionContext(now = now, dependencies = listOf(dependency)),
        )
        assertEquals(successor.id, unblockedDecision.recommended?.id)
    }

    @Test
    fun `completed skipped and deferred activities are ignored`() {
        val done = activity("done", "09:00:00", "09:30:00", Priority.REQUIRED)
            .copy(status = ActivityStatus.DONE)
        val skipped = activity("skipped", "09:30:00", "10:00:00", Priority.REQUIRED)
            .copy(status = ActivityStatus.SKIPPED)
        val deferred = activity("deferred", "10:00:00", "10:30:00", Priority.REQUIRED)
            .copy(status = ActivityStatus.DEFERRED)
        val pending = activity("pending", "11:00:00", "11:30:00", Priority.IMPORTANT)

        val decision = useCase(listOf(done, skipped, deferred, pending), NextActionContext(now))

        assertEquals(pending.id, decision.recommended?.id)
    }

    @Test
    fun `low current energy favors lower energy activity`() {
        val high = activity("high", "11:00:00", "11:30:00", Priority.IMPORTANT, Energy.HIGH)
        val low = activity("low", "11:30:00", "12:00:00", Priority.IMPORTANT, Energy.LOW)

        val decision = useCase(
            listOf(high, low),
            NextActionContext(now = now, currentEnergy = Energy.LOW),
        )

        assertEquals(low.id, decision.recommended?.id)
    }

    @Test
    fun `energy does not override mandatory priority`() {
        val high = activity("high", "11:00:00", "11:30:00", Priority.REQUIRED, Energy.HIGH)
        val low = activity("low", "11:30:00", "12:00:00", Priority.IMPORTANT, Energy.LOW)

        val decision = useCase(
            listOf(high, low),
            NextActionContext(now = now, currentEnergy = Energy.LOW),
        )

        assertEquals(high.id, decision.recommended?.id)
    }

    @Test
    fun `without current energy recommendation order remains unchanged`() {
        val high = activity("high", "11:00:00", "11:30:00", Priority.IMPORTANT, Energy.HIGH)
        val low = activity("low", "11:30:00", "12:00:00", Priority.IMPORTANT, Energy.LOW)

        val decision = useCase(listOf(high, low), NextActionContext(now))

        assertEquals(high.id, decision.recommended?.id)
    }

    private fun activity(
        id: String,
        start: String,
        end: String,
        priority: Priority,
        energy: Energy? = null,
    ): ActivityInstance {
        val startInstant = Instant.parse("2026-08-17T${start}Z")
        val endInstant = Instant.parse("2026-08-17T${end}Z")
        return ActivityInstance(
            id = ActivityInstanceId(id),
            source = ActivitySource.FromTask(TaskId(id)),
            flexibility = Flexibility.FLEXIBLE,
            planned = TimeRange(startInstant, endInstant),
            priority = priority,
            energy = energy,
        )
    }
}
