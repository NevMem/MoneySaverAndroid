package com.nevmem.moneysaver.app.dagger.modules

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.nevmem.moneysaver.app.data.Features
import com.nevmem.moneysaver.app.data.SettingsManager
import com.nevmem.moneysaver.app.data.SettingsManagerImpl
import com.nevmem.moneysaver.auth.UserHolder
import com.nevmem.moneysaver.app.data.repositories.*
import com.nevmem.moneysaver.app.room.AppDatabase
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
    fun providesUser(): com.nevmem.moneysaver.auth.UserHolder {
        return com.nevmem.moneysaver.auth.UserHolder(context)
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
    fun providesHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository = impl

    @Provides
    @Singleton
    fun infoRepository(offlineImpl: OfflineInfoRepositoryImpl, onlineImpl: OnlineInfoRepositoryImpl, settingsManager: SettingsManager): InfoRepository {
        runBlocking {
            settingsManager.initialize().join()
        }
        return if (settingsManager.isFeatureEnabled(Features.FEATURE_OFFLINE_INFO)) {
            Log.d("Dagger", "Offline impl")
            offlineImpl
        } else {
            Log.d("Dagger", "online impl")
            onlineImpl
        }
    }
}