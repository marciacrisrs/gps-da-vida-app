package com.gpsdavida.app.di

import com.gpsdavida.app.data.RoomEventRepository
import com.gpsdavida.app.data.RoomTaskRepository
import com.gpsdavida.app.domain.port.EventRepository
import com.gpsdavida.app.domain.port.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: RoomEventRepository): EventRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: RoomTaskRepository): TaskRepository
}
