package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.NetworkQueueBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule(private var context: Context) {
    @Provides
    @Singleton
    fun providesNetworkQueueBase(): NetworkQueueBase {
        return NetworkQueue(context)
    }
}