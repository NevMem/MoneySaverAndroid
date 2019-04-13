package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import com.nevmem.moneysaver.data.NetworkQueue
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule(private var context: Context) {
    @Provides
    @Singleton
    fun providesNetworkQueue(): NetworkQueue {
        return NetworkQueue(context)
    }
}