package com.gpsdavida.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppMetaEntity::class, EventEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class GpsDatabase : RoomDatabase() {
    abstract fun appMetaDao(): AppMetaDao
    abstract fun eventDao(): EventDao
}
