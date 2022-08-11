package dev.bebora.swecker.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bebora.swecker.data.AlarmRepository
import dev.bebora.swecker.data.AlarmRepositoryImpl
import dev.bebora.swecker.data.settings.SettingsRepositoryInterface
import dev.bebora.swecker.data.settings.DataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(app: Application) : SettingsRepositoryInterface {
        return DataStoreManager(app)
    }

    @Provides
    @Singleton
    fun provideAlarmRepository() : AlarmRepository {
        return AlarmRepositoryImpl()
    }
}
