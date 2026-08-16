package com.gpsdavida.app.domain

import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityId
import com.gpsdavida.app.domain.model.AvailabilityKind
import com.gpsdavida.app.domain.model.LocalTimeWindow
import com.gpsdavida.app.domain.usecase.ResolveAvailableWindows
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class AvailabilityResolverTest {
    private val resolver = ResolveAvailableWindows()

    @Test
    fun `blocked interval is subtracted from free interval`() {
        val result = resolver(
            listOf(
                rule("free", "08:00", "18:00", AvailabilityKind.FREE),
                rule("lunch", "12:00", "13:00", AvailabilityKind.BLOCKED),
            ),
        )

        assertEquals(
            listOf(
                LocalTimeWindow(LocalTime.of(8, 0), LocalTime.of(12, 0)),
                LocalTimeWindow(LocalTime.of(13, 0), LocalTime.of(18, 0)),
            ),
            result,
        )
    }

    @Test
    fun `overlapping blocked intervals are fully excluded`() {
        val result = resolver(
            listOf(
                rule("free", "08:00", "18:00", AvailabilityKind.FREE),
                rule("block1", "10:00", "13:00", AvailabilityKind.BLOCKED),
                rule("block2", "12:00", "15:00", AvailabilityKind.BLOCKED),
            ),
        )

        assertEquals(
            listOf(
                LocalTimeWindow(LocalTime.of(8, 0), LocalTime.of(10, 0)),
                LocalTimeWindow(LocalTime.of(15, 0), LocalTime.of(18, 0)),
            ),
            result,
        )
    }

    private fun rule(id: String, start: String, end: String, kind: AvailabilityKind) = Availability(
        AvailabilityId(id),
        DayOfWeek.MONDAY,
        LocalTimeWindow(LocalTime.parse(start), LocalTime.parse(end)),
        kind,
    )
}
