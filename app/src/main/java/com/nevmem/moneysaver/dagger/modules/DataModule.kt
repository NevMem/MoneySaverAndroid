package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import androidx.room.Room
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import dagger.Module
import dagger.Provides
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
}