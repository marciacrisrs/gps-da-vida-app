package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Habit
import com.gpsdavida.app.domain.model.HabitId
import com.gpsdavida.app.domain.port.HabitRepository
import javax.inject.Inject

class GetHabit @Inject constructor(
    private val habits: HabitRepository,
) {
    suspend operator fun invoke(id: HabitId): Habit? = habits.getById(id)
}
