package com.gpsdavida.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class GpsDaVidaColorsTest {
    @Test
    fun officialPaletteKeepsWarmPlannerCanvas() {
        assertEquals(Color(0xFFFBF7F4), GpsDaVidaColors.Canvas)
        assertEquals(Color(0xFFFFFDFC), GpsDaVidaColors.Surface)
    }

    @Test
    fun officialPaletteKeepsTerracottaAsPrimaryAccent() {
        assertEquals(Color(0xFFB9655F), GpsDaVidaColors.Terracotta)
        assertEquals(Color(0xFFE9C5C0), GpsDaVidaColors.TerracottaSoft)
    }

    @Test
    fun officialPaletteContainsSecondaryOrganicAccents() {
        assertEquals(Color(0xFFD88E95), GpsDaVidaColors.Rose)
        assertEquals(Color(0xFFA7B3A4), GpsDaVidaColors.Sage)
        assertEquals(Color(0xFF8C9AA7), GpsDaVidaColors.BlueGray)
    }
}
