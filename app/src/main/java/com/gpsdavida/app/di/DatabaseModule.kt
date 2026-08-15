package com.gpsdavida.app.di

import android.content.Context
import androidx.room.Room
import com.gpsdavida.app.data.local.AppMetaDao
import com.gpsdavida.app.data.local.EventDao
import com.gpsdavida.app.data.local.GpsDatabase
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

    @Provides
    fun provideAppMetaDao(database: GpsDatabase): AppMetaDao = database.appMetaDao()

    @Provides
    fun provideEventDao(database: GpsDatabase): EventDao = database.eventDao()

    @Provides
    fun provideTaskDao(database: GpsDatabase): TaskDao = database.taskDao()
}
