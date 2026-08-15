package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.EventId
import com.gpsdavida.app.domain.port.EventRepository
import javax.inject.Inject

class GetEvent @Inject constructor(
    private val events: EventRepository,
) {
    suspend operator fun invoke(id: EventId): Event? = events.getById(id)
}
