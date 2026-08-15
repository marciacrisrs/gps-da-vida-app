package com.gpsdavida.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppMetaEntity::class, EventEntity::class, TaskEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class GpsDatabase : RoomDatabase() {
    abstract fun appMetaDao(): AppMetaDao
    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
}
