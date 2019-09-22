package com.nevmem.moneysaver.app.data

abstract class Features {
    companion object {
        const val FEATURE_OFFLINE_INFO = "enable_offline_info"
        const val FEATURE_PREDICTIVE_TAGS = "predictive_tags"
        const val FEATURE_PREDICTIVE_WALLETS = "predictive_wallet"

        val availableFeatures: ArrayList<Pair<String, String>> = arrayListOf(
            Pair("Offline info", FEATURE_OFFLINE_INFO),
            Pair("Predictive tags", FEATURE_PREDICTIVE_TAGS)
        )
    }
}