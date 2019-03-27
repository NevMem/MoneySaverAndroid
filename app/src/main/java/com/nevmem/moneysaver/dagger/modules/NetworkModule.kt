package com.nevmem.moneysaver.dagger.modules

import android.content.Context
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.User
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    private var context: Context

    constructor(context: Context) {
        this.context = context
    }

    @Provides
    @Singleton
    fun providesNetworkQueue(): NetworkQueue {
        return NetworkQueue(context)
    }
}