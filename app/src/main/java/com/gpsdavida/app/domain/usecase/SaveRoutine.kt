package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Routine
import com.gpsdavida.app.domain.port.RoutineRepository
import javax.inject.Inject

class SaveRoutine @Inject constructor(
    private val routines: RoutineRepository,
) {
    suspend operator fun invoke(routine: Routine) = routines.save(routine)
}
