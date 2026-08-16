package com.gpsdavida.app.di

import android.content.Context
import androidx.room.Room
import com.gpsdavida.app.data.local.AppMetaDao
import com.gpsdavida.app.data.local.AvailabilityDao
import com.gpsdavida.app.data.local.EventDao
import com.gpsdavida.app.data.local.GpsDatabase
import com.gpsdavida.app.data.local.HabitCompletionDao
import com.gpsdavida.app.data.local.HabitDao
import com.gpsdavida.app.data.local.RoutineDao
import com.gpsdavida.app.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GpsDatabase =
        Room.databaseBuilder(context, GpsDatabase::class.java, "gps-da-vida.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideAppMetaDao(database: GpsDatabase): AppMetaDao = database.appMetaDao()
    @Provides fun provideEventDao(database: GpsDatabase): EventDao = database.eventDao()
    @Provides fun provideTaskDao(database: GpsDatabase): TaskDao = database.taskDao()
    @Provides fun provideHabitDao(database: GpsDatabase): HabitDao = database.habitDao()
    @Provides fun provideHabitCompletionDao(database: GpsDatabase): HabitCompletionDao = database.habitCompletionDao()
    @Provides fun provideRoutineDao(database: GpsDatabase): RoutineDao = database.routineDao()
    @Provides fun provideAvailabilityDao(database: GpsDatabase): AvailabilityDao = database.availabilityDao()
}
