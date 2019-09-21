package com.nevmem.moneysaver.app.data

import android.util.Log
import com.nevmem.moneysaver.app.room.AppDatabase
import com.nevmem.moneysaver.app.room.entity.Feature
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.Executor
import javax.inject.Inject

class SettingsManagerImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val executor: Executor
) : SettingsManager {
    private val enabledFeatures = HashSet<Feature>()
    private val tag = "SETTINGS_MANAGER_IMPL"

    private val listeners = ArrayList<WeakReference<SettingsManagerListener>>()

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
            notifyFeaturesChanged()
        }
    }

    override fun disableFeature(featureName: String) {
        if (isFeatureEnabled(featureName)) {
            val featureToDelete = enabledFeatures.find {
                it.featureName == featureName
            }
            enabledFeatures.remove(Feature(featureName))
            if (featureToDelete != null) {
                executor.execute {
                    appDatabase.featuresDao().delete(featureToDelete)
                }
            }
            notifyFeaturesChanged()
        }
    }

    override fun toggleFeature(featureName: String) {
        if (isFeatureEnabled(featureName)) {
            disableFeature(featureName)
        } else {
            enableFeature(featureName)
        }
    }

    override fun subscribe(listener: WeakReference<SettingsManagerListener>) {
        listeners.add(listener)
    }

    override fun unsubscribe(listener: WeakReference<SettingsManagerListener>) {
        listeners.remove(listener)
    }

    private fun notifyFeaturesChanged() {
        listeners.forEach {
            val ref = it.get()
            if (ref != null) {
                ref.onFeaturesUpdated()
            } else {
                listeners.remove(it)
            }
        }
    }
}