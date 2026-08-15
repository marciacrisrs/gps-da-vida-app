package com.gpsdavida.app.domain.port

import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.EventId
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun observeAll(): Flow<List<Event>>
    suspend fun getById(id: EventId): Event?
    suspend fun save(event: Event)
    suspend fun delete(id: EventId)
}
