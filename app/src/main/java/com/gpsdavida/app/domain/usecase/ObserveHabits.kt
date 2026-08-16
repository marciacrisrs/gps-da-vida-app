package com.gpsdavida.app.domain.usecase

import com.gpsdavida.app.domain.model.Habit
import com.gpsdavida.app.domain.port.HabitRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveHabits @Inject constructor(
    private val habits: HabitRepository,
) {
    operator fun invoke(): Flow<List<Habit>> =
        habits.observeAll().map { list -> list.sortedBy { it.title } }
}
