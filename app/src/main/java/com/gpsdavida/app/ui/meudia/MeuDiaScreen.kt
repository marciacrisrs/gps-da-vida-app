package com.gpsdavida.app.ui.meudia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpsdavida.app.R
import com.gpsdavida.app.ui.events.EventRow
import com.gpsdavida.app.ui.habits.HabitDayRow
import com.gpsdavida.app.ui.tasks.TaskRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeuDiaScreen(
    onAddEvent: () -> Unit,
    onOpenEvent: (String) -> Unit,
    onOpenTask: (String) -> Unit,
    onOpenHabit: (String) -> Unit,
    viewModel: MeuDiaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_meu_dia)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.cd_add_event))
            }
        },
    ) { padding ->
        val empty = state.events.isEmpty() && state.tasks.isEmpty() && state.habits.isEmpty()
        Column(modifier = Modifier.padding(padding)) {
            if (empty) {
                Text(
                    text = stringResource(R.string.meu_dia_empty),
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                if (state.events.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.nav_eventos),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    state.events.forEach { event ->
                        EventRow(event = event, onClick = { onOpenEvent(event.id.value) })
                    }
                }
                if (state.tasks.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.nav_tarefas),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    state.tasks.forEach { task ->
                        TaskRow(
                            task = task,
                            onClick = { onOpenTask(task.id.value) },
                            onToggleDone = { viewModel.setTaskDone(task.id.value, it) },
                        )
                    }
                }
                if (state.habits.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.nav_habitos),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    state.habits.forEach { habitDay ->
                        HabitDayRow(
                            item = habitDay,
                            onClick = { onOpenHabit(habitDay.habit.id.value) },
                            onToggleDone = { viewModel.setHabitDone(habitDay.habit.id.value, it) },
                        )
                    }
                }
            }
        }
    }
}
