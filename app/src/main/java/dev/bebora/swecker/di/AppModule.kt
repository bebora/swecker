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
import dev.bebora.swecker.data.service.AccountService
import dev.bebora.swecker.data.service.ImageStorageService
import dev.bebora.swecker.data.service.StorageService
import dev.bebora.swecker.data.service.impl.AccountServiceImpl
import dev.bebora.swecker.data.service.impl.ImageStorageServiceImpl
import dev.bebora.swecker.data.service.impl.StorageServiceImpl
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
    fun provideAlarmRepository(db: SweckerDatabase): AlarmRepository {
        return AlarmRepositoryImpl(db.alarmDao)
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
    fun provideAccountService(): AccountService {
        return AccountServiceImpl()
    }

    @Provides
    @Singleton
    fun provideImageStorageService(): ImageStorageService {
        return ImageStorageServiceImpl()
    }

    @Provides
    @Singleton
    fun provideStorageService(): StorageService {
        return StorageServiceImpl()
    }
}
