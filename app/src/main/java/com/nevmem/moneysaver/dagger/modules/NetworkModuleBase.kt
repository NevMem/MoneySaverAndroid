package com.nevmem.moneysaver.dagger.modules

import com.nevmem.moneysaver.data.NetworkQueueBase

interface NetworkModuleBase {
    fun providesNetworkQueueBase(): NetworkQueueBase
}