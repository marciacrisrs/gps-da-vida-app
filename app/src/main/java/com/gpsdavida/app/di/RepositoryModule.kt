package com.gpsdavida.app.di

import com.gpsdavida.app.data.RoomEventRepository
import com.gpsdavida.app.domain.port.EventRepository
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
}
