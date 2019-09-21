package com.nevmem.moneysaver.app.dagger.modules

import com.nevmem.moneysaver.app.data.NetworkQueueBase

interface NetworkModuleBase {
    fun providesNetworkQueueBase(): NetworkQueueBase
}