package com.nevmem.moneysaver.app.data

abstract class Features {
    companion object {
        const val FEATURE_OFFLINE_INFO = "enable_offline_info"

        val availableFeatures: ArrayList<Pair<String, String>> = arrayListOf(
            Pair("Offline info", FEATURE_OFFLINE_INFO)
        )
    }
}