package com.nevmem.moneysaver.app.dagger.modules

import android.content.Context
import com.nevmem.moneysaver.app.data.NetworkQueue
import com.nevmem.moneysaver.app.data.NetworkQueueBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule(private var context: Context) : NetworkModuleBase {
    @Provides
    @Singleton
    override fun providesNetworkQueueBase(): NetworkQueueBase {
        return NetworkQueue(context)
    }
}