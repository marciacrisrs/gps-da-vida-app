package com.gpsdavida.app.ui.meudia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpsdavida.app.R
import com.gpsdavida.app.ui.events.EventRow
import com.gpsdavida.app.ui.habits.HabitDayRow
import com.gpsdavida.app.ui.tasks.TaskRow
import com.gpsdavida.app.ui.theme.GpsDaVidaColors
import com.gpsdavida.app.ui.theme.GpsDaVidaSpacing
import com.gpsdavida.app.ui.theme.SuperPlannerLogo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeuDiaScreen(
    onAddEvent: () -> Unit,
    onOpenEvent: (String) -> Unit,
    onOpenTask: (String) -> Unit,
    onOpenHabit: (String) -> Unit,
    onOpenAvailability: () -> Unit,
    viewModel: MeuDiaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val totalItems = state.events.size + state.tasks.size + state.habits.size

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { SuperPlannerLogo(iconSize = 36.dp) },
                actions = {
                    IconButton(onClick = onOpenAvailability) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.cd_availability))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_add_event))
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = GpsDaVidaSpacing.Page, vertical = GpsDaVidaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Lg),
        ) {
            item { PlannerHeader() }
            if (totalItems == 0) {
                item { PlannerEmptyState() }
            } else {
                item { DaySummary(state.events.size, state.tasks.size, state.habits.size) }
                if (state.events.isNotEmpty()) {
                    item { SectionTitle(stringResource(R.string.nav_eventos), GpsDaVidaColors.Rose) }
                    items(state.events, key = { "event-${it.id.value}" }) { event ->
                        PlannerItemCard(GpsDaVidaColors.Rose) { EventRow(event = event, onClick = { onOpenEvent(event.id.value) }) }
                    }
                }
                if (state.tasks.isNotEmpty()) {
                    item { SectionTitle(stringResource(R.string.nav_tarefas), GpsDaVidaColors.Terracotta) }
                    items(state.tasks, key = { "task-${it.id.value}" }) { task ->
                        PlannerItemCard(GpsDaVidaColors.Terracotta) {
                            TaskRow(task = task, onClick = { onOpenTask(task.id.value) }, onToggleDone = { viewModel.setTaskDone(task.id.value, it) })
                        }
                    }
                }
                if (state.habits.isNotEmpty()) {
                    item { SectionTitle(stringResource(R.string.nav_habitos), GpsDaVidaColors.Sage) }
                    items(state.habits, key = { "habit-${it.habit.id.value}" }) { habitDay ->
                        PlannerItemCard(GpsDaVidaColors.Sage) {
                            HabitDayRow(item = habitDay, onClick = { onOpenHabit(habitDay.habit.id.value) }, onToggleDone = { viewModel.setHabitDone(habitDay.habit.id.value, it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerHeader() {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    Column(verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Sm)) {
        Text("Meu dia", style = MaterialTheme.typography.displayLarge)
        Text(today.format(formatter).replaceFirstChar { it.titlecase(Locale("pt", "BR")) }, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DaySummary(events: Int, tasks: Int, habits: Int) {
    val total = events + tasks + habits
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = GpsDaVidaColors.SurfaceWarm)) {
        Row(modifier = Modifier.padding(GpsDaVidaSpacing.Xl), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Xl)) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(GpsDaVidaColors.TerracottaSoft), contentAlignment = Alignment.Center) {
                Text(total.toString(), style = MaterialTheme.typography.titleLarge, color = GpsDaVidaColors.TerracottaDark, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Xs)) {
                Text("Seu dia em movimento", style = MaterialTheme.typography.titleMedium)
                Text("$events eventos • $tasks tarefas • $habits hábitos", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, marker: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Sm)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(marker))
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PlannerItemCard(marker: Color, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(5.dp).height(76.dp).background(marker))
            Box(modifier = Modifier.padding(vertical = GpsDaVidaSpacing.Sm)) { content() }
        }
    }
}

@Composable
private fun PlannerEmptyState() {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(containerColor = GpsDaVidaColors.SurfaceWarm)) {
        Column(modifier = Modifier.padding(GpsDaVidaSpacing.Xxl), verticalArrangement = Arrangement.spacedBy(GpsDaVidaSpacing.Md)) {
            Text("Seu dia está livre por enquanto.", style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.meu_dia_empty), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
