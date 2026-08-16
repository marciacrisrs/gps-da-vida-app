package com.gpsdavida.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppMetaEntity::class,
        EventEntity::class,
        TaskEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        RoutineEntity::class,
        RoutineStepEntity::class,
        AvailabilityEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class GpsDatabase : RoomDatabase() {
    abstract fun appMetaDao(): AppMetaDao
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun routineDao(): RoutineDao
    abstract fun availabilityDao(): AvailabilityDao
}
