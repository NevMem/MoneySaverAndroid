package com.nevmem.moneysaver.app.data

import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

interface SettingsManagerListener {
    fun onFeaturesUpdated()
}

interface SettingsManager {
    fun initialize(): Job

    fun isFeatureEnabled(featureName: String): Boolean
    fun enableFeature(featureName: String)
    fun disableFeature(featureName: String)
    fun toggleFeature(featureName: String)

    fun subscribe(listener: WeakReference<SettingsManagerListener>)
    fun unsubscribe(listener: WeakReference<SettingsManagerListener>)
}