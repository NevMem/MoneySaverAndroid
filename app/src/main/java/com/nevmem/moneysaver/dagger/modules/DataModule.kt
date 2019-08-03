package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.nevmem.moneysaver.data.Features
import com.nevmem.moneysaver.data.SettingsManager
import com.nevmem.moneysaver.data.SettingsManagerImpl
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.repositories.InfoRepository
import com.nevmem.moneysaver.data.repositories.OfflineInfoRepositoryImpl
import com.nevmem.moneysaver.data.repositories.OnlineInfoRepositoryImpl
import com.nevmem.moneysaver.room.AppDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class DataModule(private var context: Context) {
    @Provides
    @Singleton
    fun providesUser(): UserHolder {
        return UserHolder(context)
    }

    @Provides
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesExecutor(): Executor {
        return Executors.newFixedThreadPool(1)
    }

    @Provides
    @Singleton
    fun providesAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "moneysaverdb")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesSettingsManager(impl: SettingsManagerImpl): SettingsManager {
        return impl
    }

    @Provides
    @Singleton
    fun infoRepository(offlineImpl: OfflineInfoRepositoryImpl, onlineImpl: OnlineInfoRepositoryImpl, settingsManager: SettingsManager): InfoRepository {
        runBlocking {
            settingsManager.initialize().join()
        }
        return if (settingsManager.isFeatureEnabled(Features.FEATURE_OFFLINE_INFO)) {
            offlineImpl
        } else {
            onlineImpl
        }
    }
}