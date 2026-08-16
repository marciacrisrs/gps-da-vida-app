package com.gpsdavida.app.data

import com.gpsdavida.app.data.local.RoutineDao
import com.gpsdavida.app.data.mapper.toDomain
import com.gpsdavida.app.data.mapper.toEntity
import com.gpsdavida.app.data.mapper.toStepEntities
import com.gpsdavida.app.domain.model.Routine
import com.gpsdavida.app.domain.model.RoutineId
import com.gpsdavida.app.domain.port.RoutineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RoomRoutineRepository @Inject constructor(
    private val dao: RoutineDao,
) : RoutineRepository {
    override fun observeAll(): Flow<List<Routine>> = combine(
        dao.observeAll(),
        dao.observeAllSteps(),
    ) { rows, steps ->
        val stepsByRoutine = steps.groupBy { it.routineId }
        rows.map { it.toDomain(stepsByRoutine[it.id].orEmpty()) }
    }

    override suspend fun getById(id: RoutineId): Routine? =
        dao.getById(id.value)?.let { it.toDomain(dao.getSteps(id.value)) }

    override suspend fun save(routine: Routine) {
        dao.replaceRoutine(routine.toEntity(), routine.toStepEntities())
    }

    override suspend fun delete(id: RoutineId) {
        dao.delete(id.value)
    }
}
