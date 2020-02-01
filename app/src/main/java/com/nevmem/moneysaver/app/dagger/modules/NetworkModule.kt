package com.nevmem.moneysaver.app.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule(private var context: Context) : NetworkModuleBase {
    @Provides
    @Singleton
    override fun providesNetworkQueueBase(): com.nevmem.moneysaver.network.NetworkQueue {
        return com.nevmem.moneysaver.network.NetworkQueueImpl(context)
    }
}