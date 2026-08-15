package com.gpsdavida.app.ui.meudia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.usecase.ObserveEventsForDay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MeuDiaViewModel @Inject constructor(
    observeEventsForDay: ObserveEventsForDay,
) : ViewModel() {
    val events: StateFlow<List<Event>> = observeEventsForDay()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
