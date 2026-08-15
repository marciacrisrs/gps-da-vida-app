package com.gpsdavida.app.ui.meudia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.Task
import com.gpsdavida.app.domain.model.TaskId
import com.gpsdavida.app.domain.usecase.CompleteTask
import com.gpsdavida.app.domain.usecase.ObserveEventsForDay
import com.gpsdavida.app.domain.usecase.ObserveTasksForDay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MeuDiaUiState(
    val events: List<Event> = emptyList(),
    val tasks: List<Task> = emptyList(),
)

@HiltViewModel
class MeuDiaViewModel @Inject constructor(
    observeEventsForDay: ObserveEventsForDay,
    observeTasksForDay: ObserveTasksForDay,
    private val completeTask: CompleteTask,
) : ViewModel() {
    val state: StateFlow<MeuDiaUiState> = combine(
        observeEventsForDay(),
        observeTasksForDay(),
    ) { events, tasks -> MeuDiaUiState(events, tasks) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MeuDiaUiState())

    fun setTaskDone(id: String, done: Boolean) {
        viewModelScope.launch { completeTask(TaskId(id), done) }
    }
}
