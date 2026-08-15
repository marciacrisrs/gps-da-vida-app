package com.gpsdavida.app.data

import com.gpsdavida.app.data.local.EventDao
import com.gpsdavida.app.data.mapper.toDomain
import com.gpsdavida.app.data.mapper.toEntity
import com.gpsdavida.app.domain.model.Event
import com.gpsdavida.app.domain.model.EventId
import com.gpsdavida.app.domain.port.EventRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomEventRepository @Inject constructor(
    private val dao: EventDao,
) : EventRepository {
    override fun observeAll(): Flow<List<Event>> = dao.observeAll().map { rows ->
        rows.map { it.toDomain() }
    }

    override suspend fun getById(id: EventId): Event? = dao.getById(id.value)?.toDomain()

    override suspend fun save(event: Event) {
        dao.upsert(event.toEntity())
    }

    override suspend fun delete(id: EventId) {
        dao.delete(id.value)
    }
}
