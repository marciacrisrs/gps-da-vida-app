package com.gpsdavida.app.ui.next

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpsdavida.app.R
import com.gpsdavida.app.ui.theme.GpsDaVidaColors
import com.gpsdavida.app.ui.theme.GpsDaVidaSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Presentation-only state for the next-action surface.
 * Planning decisions belong to the domain; this component only renders them.
 */
data class NextActionUiModel(
    val title: String,
    val durationMinutes: Long? = null,
    val scheduledTime: LocalTime? = null,
    val priorityLabel: String? = null,
    val contextLabel: String? = null,
    val state: NextActionState = NextActionState.Ready,
)

enum class NextActionState {
    Ready,
    InProgress,
    Completed,
    Empty,
}

@Composable
fun NextActionCard(
    model: NextActionUiModel,
    onStart: () -> Unit = {},
    onSnooze: () -> Unit = {},
    onComplete: () -> Unit = {},
    onSwap: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = GpsDaVidaColors.SurfaceWarm),
    ) {
        when (model.state) {
            NextActionState.Empty -> EmptyContent()
            NextActionState.Completed -> CompletedContent(model.title)
            NextActionState.Ready, NextActionState.InProgress -> ReadyContent(
                model = model,
                onStart = onStart,
                onSnooze = onSnooze,
                onComplete = onComplete,
                onSwap = onSwap,
            )
        }
    }
}

@Composable
private fun ReadyContent(
    model: NextActionUiModel,
    onStart: () -> Unit,
    onSnooze: () -> Unit,
    onComplete: () -> Unit,
    onSwap: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(GpsDaVidaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Md),
    ) {
        NextLabel()

        Text(
            text = model.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )

        MetadataRow(model)

        Spacer(modifier = Modifier.size(GpsDaVidaSpacing.Xs))

        Button(
            onClick = if (model.state == NextActionState.Ready) onStart else onComplete,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = if (model.state == NextActionState.Ready) Icons.Filled.PlayArrow else Icons.Filled.Check,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(GpsDaVidaSpacing.Sm))
            Text(
                text = stringResource(
                    if (model.state == NextActionState.Ready) R.string.next_action_start else R.string.next_action_complete,
                ),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onSnooze) {
                Icon(Icons.Filled.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.size(GpsDaVidaSpacing.Xs))
                Text(stringResource(R.string.next_action_snooze))
            }
            IconButton(onClick = onSwap) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.next_action_swap),
                )
            }
        }
    }
}

@Composable
private fun MetadataRow(model: NextActionUiModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        model.scheduledTime?.let { time ->
            MetadataItem(
                icon = Icons.Filled.AccessTime,
                text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                tint = GpsDaVidaColors.TerracottaDark,
            )
        }
        model.durationMinutes?.let { minutes ->
            MetadataItem(
                icon = Icons.Filled.AccessTime,
                text = stringResource(R.string.next_action_duration, minutes),
                tint = GpsDaVidaColors.Warning,
            )
        }
        model.priorityLabel?.let { priority ->
            MetadataItem(
                icon = Icons.Filled.Star,
                text = priority,
                tint = GpsDaVidaColors.Rose,
            )
        }
        model.contextLabel?.let { context ->
            MetadataItem(
                icon = Icons.Filled.MoreVert,
                text = context,
                tint = GpsDaVidaColors.Sage,
            )
        }
    }
}

@Composable
private fun MetadataItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Xs),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun NextLabel() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Sm)) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = GpsDaVidaColors.Terracotta,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(R.string.next_action_label),
            style = MaterialTheme.typography.labelLarge,
            color = GpsDaVidaColors.TerracottaDark,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier.padding(GpsDaVidaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Md),
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = GpsDaVidaColors.Sage,
            modifier = Modifier.size(28.dp),
        )
        Text(stringResource(R.string.next_action_empty_title), style = MaterialTheme.typography.headlineSmall)
        Text(
            stringResource(R.string.next_action_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompletedContent(title: String) {
    Row(
        modifier = Modifier.padding(GpsDaVidaSpacing.Xxl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Md),
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = GpsDaVidaColors.Success,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
        )
        Column {
            Text(stringResource(R.string.next_action_completed_label), style = MaterialTheme.typography.labelLarge, color = GpsDaVidaColors.Success)
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}
