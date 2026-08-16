package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Habit
import com.gpsdavida.app.domain.port.HabitRepository
import javax.inject.Inject

class SaveHabit @Inject constructor(
    private val habits: HabitRepository,
) {
    suspend operator fun invoke(habit: Habit) {
        require(habit.title.isNotBlank()) { "title" }
        require(!habit.plannedDuration.isNegative && !habit.plannedDuration.isZero) { "duration" }
        habits.save(habit)
    }
}
