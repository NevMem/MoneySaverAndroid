package com.nevmem.moneysaver.app.dagger.modules

import com.nevmem.moneysaver.network.NetworkQueue

interface NetworkModuleBase {
    fun providesNetworkQueueBase(): NetworkQueue
}