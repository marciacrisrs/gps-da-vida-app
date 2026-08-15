package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.EventId
import com.gpsdavida.app.domain.port.EventRepository
import javax.inject.Inject

class DeleteEvent @Inject constructor(
    private val events: EventRepository,
) {
    suspend operator fun invoke(id: EventId) {
        events.delete(id)
    }
}
