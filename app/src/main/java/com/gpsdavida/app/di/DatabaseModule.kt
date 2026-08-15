package com.gpsdavida.app.di

import android.content.Context
import androidx.room.Room
import com.gpsdavida.app.data.local.AppMetaDao
import com.gpsdavida.app.data.local.GpsDatabase
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
            .build()

    @Provides
    fun provideAppMetaDao(database: GpsDatabase): AppMetaDao = database.appMetaDao()
}
