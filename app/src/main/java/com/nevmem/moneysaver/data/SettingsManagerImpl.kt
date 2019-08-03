package com.nevmem.moneysaver.data

import android.util.Log
import com.nevmem.moneysaver.room.AppDatabase
import com.nevmem.moneysaver.room.dao.FeaturesDao_Impl
import com.nevmem.moneysaver.room.entity.Feature
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

class SettingsManagerImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val executor: Executor
) : SettingsManager {
    private val enabledFeatures = HashSet<Feature>()
    private val tag = "SETTINGS_MANAGER_IMPL"

    private val listeners = ArrayList<SettingsManagerListener>()

    init {
        Log.d(tag, "Initialization")
    }

    private var initialization = GlobalScope.launch {
        appDatabase.featuresDao().loadAll().forEach {
            Log.d(tag, "Feature: ${it.featureName}")
            enabledFeatures.add(it)
        }
    }

    override fun initialize(): Job = initialization


    override fun isFeatureEnabled(featureName: String): Boolean {
        return enabledFeatures.contains(Feature(featureName))
    }

    override fun enableFeature(featureName: String) {
        if (!isFeatureEnabled(featureName)) {
            enabledFeatures.add(Feature(featureName))
            executor.execute {
                appDatabase.featuresDao().insert(Feature(featureName))
            }
        }
    }

    override fun disableFeature(featureName: String) {
        if (isFeatureEnabled(featureName)) {
            enabledFeatures.remove(Feature(featureName))
            executor.execute {
                appDatabase.featuresDao().delete(Feature(featureName))
            }
        }
    }

    override fun toggleFeature(featureName: String) {
        if (isFeatureEnabled(featureName)) {
            disableFeature(featureName)
        } else {
            enableFeature(featureName)
        }
    }

    override fun subscribe(listener: SettingsManagerListener) {
        listeners.add(listener)
    }

    override fun unsubscribe(listener: SettingsManagerListener) {
        listeners.remove(listener)
    }

    private fun notifyFeaturesChanged() {
        listeners.forEach {
            it.onFeaturesUpdated()
        }
    }
}