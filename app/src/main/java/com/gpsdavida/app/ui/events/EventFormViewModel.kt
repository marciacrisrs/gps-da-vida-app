package com.gpsdavida.app.ui.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.EventId
import com.gpsdavida.app.domain.model.TimeRange
import com.gpsdavida.app.domain.usecase.DeleteEvent
import com.gpsdavida.app.domain.usecase.GetEvent
import com.gpsdavida.app.domain.usecase.SaveEvent
import com.gpsdavida.app.ui.navigation.GpsRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventFormUiState(
    val isNew: Boolean = true,
    val title: String = "",
    val start: Instant = Instant.EPOCH,
    val end: Instant = Instant.EPOCH,
    val recurrenceDays: Set<DayOfWeek> = emptySet(),
    val error: EventFormError? = null,
    val finished: Boolean = false,
)

enum class EventFormError { BLANK_TITLE, INVALID_RANGE }

@HiltViewModel
class EventFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clock: Clock,
    private val getEvent: GetEvent,
    private val saveEvent: SaveEvent,
    private val deleteEvent: DeleteEvent,
) : ViewModel() {

    private val eventIdArg: String = checkNotNull(savedStateHandle["eventId"])
    private val eventId: EventId =
        if (eventIdArg == GpsRoutes.NEW_EVENT_ID) EventId(UUID.randomUUID().toString())
        else EventId(eventIdArg)

    private val _state = MutableStateFlow(defaultState())
    val state: StateFlow<EventFormUiState> = _state.asStateFlow()

    init {
        if (eventIdArg != GpsRoutes.NEW_EVENT_ID) {
            viewModelScope.launch {
                val existing = getEvent(eventId) ?: return@launch
                _state.value = EventFormUiState(
                    isNew = false,
                    title = existing.title,
                    start = existing.range.start,
                    end = existing.range.end,
                    recurrenceDays = existing.recurrenceDays,
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _state.update { it.copy(title = value, error = null) }
    }

    fun onStartDate(date: LocalDate) {
        _state.update { it.copy(start = it.start.withDate(date), error = null) }
    }

    fun onStartTime(time: LocalTime) {
        _state.update { it.copy(start = it.start.withTime(time), error = null) }
    }

    fun onEndDate(date: LocalDate) {
        _state.update { it.copy(end = it.end.withDate(date), error = null) }
    }

    fun onEndTime(time: LocalTime) {
        _state.update { it.copy(end = it.end.withTime(time), error = null) }
    }

    fun toggleDay(day: DayOfWeek) {
        _state.update { current ->
            val days = current.recurrenceDays.toMutableSet()
            if (!days.add(day)) days.remove(day)
            current.copy(recurrenceDays = days)
        }
    }

    fun save() {
        val current = _state.value
        if (current.title.isBlank()) {
            _state.update { it.copy(error = EventFormError.BLANK_TITLE) }
            return
        }
        val range = runCatching { TimeRange(current.start, current.end) }.getOrNull()
        if (range == null) {
            _state.update { it.copy(error = EventFormError.INVALID_RANGE) }
            return
        }
        viewModelScope.launch {
            saveEvent(
                Event(
                    id = eventId,
                    title = current.title.trim(),
                    range = range,
                    recurrenceDays = current.recurrenceDays,
                ),
            )
            _state.update { it.copy(finished = true) }
        }
    }

    fun delete() {
        viewModelScope.launch {
            deleteEvent(eventId)
            _state.update { it.copy(finished = true) }
        }
    }

    private fun defaultState(): EventFormUiState {
        val start = ZonedDateTime.now(clock).withMinute(0).withSecond(0).withNano(0).plusHours(1)
        return EventFormUiState(
            isNew = true,
            start = start.toInstant(),
            end = start.plusHours(1).toInstant(),
        )
    }

    private fun Instant.withDate(date: LocalDate): Instant {
        val zoned = atZone(clock.zone)
        return ZonedDateTime.of(date, zoned.toLocalTime(), clock.zone).toInstant()
    }

    private fun Instant.withTime(time: LocalTime): Instant {
        val zoned = atZone(clock.zone)
        return ZonedDateTime.of(zoned.toLocalDate(), time, clock.zone).toInstant()
    }
}
