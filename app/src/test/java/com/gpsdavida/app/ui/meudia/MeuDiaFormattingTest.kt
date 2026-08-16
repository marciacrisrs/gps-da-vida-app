package com.gpsdavida.app.ui.meudia

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class MeuDiaFormattingTest {
    @Test
    fun formatsPlannerDateInPortuguese() {
        assertEquals("Domingo, 16 de agosto", formatPlannerDate(LocalDate.of(2026, 8, 16)))
    }
}
