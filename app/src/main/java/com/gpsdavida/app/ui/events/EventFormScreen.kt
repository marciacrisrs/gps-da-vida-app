package com.gpsdavida.app.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpsdavida.app.R
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventFormScreen(
    onDone: () -> Unit,
    viewModel: EventFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.finished) {
        if (state.finished) onDone()
    }

    var confirmDelete by remember { mutableStateOf(false) }
    val zone = ZoneId.systemDefault()
    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm", Locale("pt", "BR"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isNew) R.string.event_new else R.string.event_edit,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                actions = {
                    if (!state.isNew) {
                        IconButton(onClick = { confirmDelete = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.event_delete),
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.event_title)) },
                isError = state.error == EventFormError.BLANK_TITLE,
                supportingText = {
                    if (state.error == EventFormError.BLANK_TITLE) {
                        Text(stringResource(R.string.event_title_required))
                    }
                },
            )
            DateTimeRow(
                label = stringResource(R.string.event_start),
                instant = state.start,
                zone = zone,
                dateFmt = dateFmt,
                timeFmt = timeFmt,
                onDate = viewModel::onStartDate,
                onTime = viewModel::onStartTime,
            )
            DateTimeRow(
                label = stringResource(R.string.event_end),
                instant = state.end,
                zone = zone,
                dateFmt = dateFmt,
                timeFmt = timeFmt,
                onDate = viewModel::onEndDate,
                onTime = viewModel::onEndTime,
            )
            if (state.error == EventFormError.INVALID_RANGE) {
                Text(stringResource(R.string.event_invalid_range))
            }
            Text(stringResource(R.string.event_recurrence))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recurrenceOptions().forEach { (day, labelRes) ->
                    FilterChip(
                        selected = day in state.recurrenceDays,
                        onClick = { viewModel.toggleDay(day) },
                        label = { Text(stringResource(labelRes)) },
                    )
                }
            }
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.event_save))
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.event_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        viewModel.delete()
                    },
                ) { Text(stringResource(R.string.event_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun DateTimeRow(
    label: String,
    instant: Instant,
    zone: ZoneId,
    dateFmt: DateTimeFormatter,
    timeFmt: DateTimeFormatter,
    onDate: (LocalDate) -> Unit,
    onTime: (LocalTime) -> Unit,
) {
    val zoned = instant.atZone(zone)
    var pickDate by remember { mutableStateOf(false) }
    var pickTime by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { pickDate = true }) {
                Text("${stringResource(R.string.event_date)}: ${zoned.format(dateFmt)}")
            }
            OutlinedButton(onClick = { pickTime = true }) {
                Text("${stringResource(R.string.event_time)}: ${zoned.format(timeFmt)}")
            }
        }
    }
    if (pickDate) {
        DatePickerSheet(
            initial = zoned.toLocalDate(),
            onDismiss = { pickDate = false },
            onConfirm = {
                onDate(it)
                pickDate = false
            },
        )
    }
    if (pickTime) {
        TimePickerSheet(
            initial = zoned.toLocalTime(),
            onDismiss = { pickTime = false },
            onConfirm = {
                onTime(it)
                pickTime = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSheet(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis ?: return@TextButton
                    onConfirm(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                },
            ) { Text(stringResource(R.string.action_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerSheet(
    initial: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val state = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        text = { TimePicker(state = state) },
    )
}

private fun recurrenceOptions(): List<Pair<DayOfWeek, Int>> = listOf(
    DayOfWeek.MONDAY to R.string.day_mon,
    DayOfWeek.TUESDAY to R.string.day_tue,
    DayOfWeek.WEDNESDAY to R.string.day_wed,
    DayOfWeek.THURSDAY to R.string.day_thu,
    DayOfWeek.FRIDAY to R.string.day_fri,
    DayOfWeek.SATURDAY to R.string.day_sat,
    DayOfWeek.SUNDAY to R.string.day_sun,
)
