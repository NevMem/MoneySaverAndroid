package com.nevmem.moneysaver.data

import kotlinx.coroutines.Job

interface SettingsManagerListener {
    fun onFeaturesUpdated()
}

interface SettingsManager {
    fun initialize(): Job

    fun isFeatureEnabled(featureName: String): Boolean
    fun enableFeature(featureName: String)
    fun disableFeature(featureName: String)
    fun toggleFeature(featureName: String)

    fun subscribe(listener: SettingsManagerListener)
    fun unsubscribe(listener: SettingsManagerListener)
}