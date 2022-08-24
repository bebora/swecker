package dev.bebora.swecker.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bebora.swecker.data.SweckerDatabase
import dev.bebora.swecker.data.alarm_browser.AlarmRepository
import dev.bebora.swecker.data.alarm_browser.AlarmRepositoryImpl
import dev.bebora.swecker.data.service.*
import dev.bebora.swecker.data.service.impl.*
import dev.bebora.swecker.data.settings.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepositoryInterface {
        return FakeSettingsRepository()
    }

    // TODO provide fake services
    @Provides
    @Singleton
    fun provideAlarmRepository(db: SweckerDatabase): AlarmRepository {
        return AlarmRepositoryImpl(db.alarmDao, provideAccountService() ,provideAlarmProviderService())
    }

    @Provides
    @Singleton
    fun provideSweckerDatabase(app: Application): SweckerDatabase {
        return Room.inMemoryDatabaseBuilder(
            app,
            SweckerDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAccountService(): AuthService {
        return AuthServiceImpl()
    }

    @Provides
    @Singleton
    fun provideImageStorageService(): ImageStorageService {
        return ImageStorageServiceImpl()
    }

    @Provides
    @Singleton
    fun provideStorageService(): AccountsService {
        return AccountsServiceImpl()
    }

    @Provides
    @Singleton
    fun provideChatService(): ChatService {
        return ChatServiceImpl()
    }

    @Provides
    @Singleton
    fun provideAlarmProviderService(): AlarmProviderService {
        return AlarmProviderServiceImpl()
    }
}
