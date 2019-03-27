package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.room.Room
import com.nevmem.moneysaver.data.TemplatesRepository
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {
    private var context: Context

    constructor(context: Context) {
        this.context = context
    }

    @Provides
    @Singleton
    fun providesUser(): UserHolder {
        return UserHolder(context)
    }

    @Provides
    @Singleton
    fun providesAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "moneysaverdb").build()
    }
}