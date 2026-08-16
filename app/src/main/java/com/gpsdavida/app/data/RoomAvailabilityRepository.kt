package com.gpsdavida.app.data

import com.gpsdavida.app.data.local.AvailabilityDao
import com.gpsdavida.app.data.mapper.toDomain
import com.gpsdavida.app.data.mapper.toEntity
import com.gpsdavida.app.domain.model.Availability
import com.gpsdavida.app.domain.model.AvailabilityId
import com.gpsdavida.app.domain.port.AvailabilityRepository
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAvailabilityRepository @Inject constructor(
    private val dao: AvailabilityDao,
) : AvailabilityRepository {
    override fun observeAll(): Flow<List<Availability>> = dao.observeAll().map { list ->
        list.map { it.toDomain() }
    }

    override fun observeForDay(dayOfWeek: DayOfWeek): Flow<List<Availability>> =
        dao.observeForDay(dayOfWeek.value).map { list -> list.map { it.toDomain() } }

    override suspend fun save(availability: Availability) = dao.upsert(availability.toEntity())

    override suspend fun delete(id: AvailabilityId) = dao.delete(id.value)
}