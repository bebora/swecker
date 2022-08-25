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
import dev.bebora.swecker.data.settings.SettingsRepositoryInterface
import dev.bebora.swecker.data.settings.DataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(app: Application): SettingsRepositoryInterface {
        return DataStoreManager(app)
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(
        db: SweckerDatabase,
        authService: AuthService,
        alarmProviderService: AlarmProviderService
    ): AlarmRepository {
        return AlarmRepositoryImpl(db.alarmDao, authService, alarmProviderService)
    }

    @Provides
    @Singleton
    fun provideSweckerDatabase(app: Application): SweckerDatabase {
        return Room.databaseBuilder(
            app,
            SweckerDatabase::class.java,
            "swecker_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthServiceImpl()
    }

    @Provides
    @Singleton
    fun provideImageStorageService(): ImageStorageService {
        return ImageStorageServiceImpl()
    }

    @Provides
    @Singleton
    fun provideAccountsService(): AccountsService {
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
