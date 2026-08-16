package com.gpsdavida.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GpsDaVidaLightColors = lightColorScheme(
    primary = GpsDaVidaColors.Terracotta,
    onPrimary = GpsDaVidaColors.Surface,
    primaryContainer = GpsDaVidaColors.TerracottaSoft,
    onPrimaryContainer = GpsDaVidaColors.TerracottaDark,
    secondary = GpsDaVidaColors.Rose,
    onSecondary = GpsDaVidaColors.Ink,
    secondaryContainer = GpsDaVidaColors.RoseSoft,
    onSecondaryContainer = GpsDaVidaColors.Ink,
    tertiary = GpsDaVidaColors.Sage,
    onTertiary = GpsDaVidaColors.Ink,
    tertiaryContainer = GpsDaVidaColors.SageSoft,
    onTertiaryContainer = GpsDaVidaColors.Ink,
    background = GpsDaVidaColors.Canvas,
    onBackground = GpsDaVidaColors.Ink,
    surface = GpsDaVidaColors.Surface,
    onSurface = GpsDaVidaColors.Ink,
    surfaceVariant = GpsDaVidaColors.SurfaceWarm,
    onSurfaceVariant = GpsDaVidaColors.InkSoft,
    outline = GpsDaVidaColors.Outline,
    error = GpsDaVidaColors.Error,
)

@Composable
fun GpsDaVidaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GpsDaVidaLightColors,
        typography = GpsDaVidaTypography,
        shapes = GpsDaVidaShapes,
        content = content,
    )
}
