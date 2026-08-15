package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.port.EventRepository
import javax.inject.Inject

class SaveEvent @Inject constructor(
    private val events: EventRepository,
) {
    suspend operator fun invoke(event: Event) {
        require(event.title.isNotBlank()) { "title" }
        events.save(event)
    }
}
