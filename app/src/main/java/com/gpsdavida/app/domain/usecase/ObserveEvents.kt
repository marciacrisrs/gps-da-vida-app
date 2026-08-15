package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.port.EventRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveEvents @Inject constructor(
    private val events: EventRepository,
) {
    operator fun invoke(): Flow<List<Event>> =
        events.observeAll().map { list -> list.sortedBy { it.range.start } }
}
