package com.gpsdavida.app.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Reusable visual primitives for the Super Planner editorial language. */
@Composable
fun SuperPlannerCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = GpsDaVidaColors.SurfaceWarm),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content,
    )
}

@Composable
fun SuperPlannerPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GpsDaVidaColors.Terracotta,
            contentColor = GpsDaVidaColors.Surface,
        ),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SuperPlannerSecondaryActions(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onSecondary,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedButtonDefaults.colors(contentColor = GpsDaVidaColors.InkSoft),
        ) {
            Text(secondaryText, style = MaterialTheme.typography.labelMedium)
        }
        Button(
            onClick = onPrimary,
            modifier = Modifier.weight(1.35f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GpsDaVidaColors.Terracotta,
                contentColor = GpsDaVidaColors.Surface,
            ),
        ) {
            Text(primaryText, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SuperPlannerMetadata(
    items: List<Pair<String, androidx.compose.ui.graphics.Color>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEach { (text, color) ->
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
            )
        }
    }
}
