package com.nevmem.moneysaver.app.activity.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.app.data.Features
import com.nevmem.moneysaver.app.data.SettingsManager
import com.nevmem.moneysaver.app.data.SettingsManagerListener
import com.nevmem.moneysaver.common.data.ToggleableFeature
import java.lang.ref.WeakReference
import javax.inject.Inject

class DevSettingsPageViewModel(app: Application): AndroidViewModel(app), SettingsManagerListener {
    @Inject
    lateinit var settingsManager: SettingsManager

    private val features = MutableLiveData<List<ToggleableFeature>>()
    fun features(): LiveData<List<ToggleableFeature>> = features

    init {
        app as App
        app.appComponent.inject(this)

        settingsManager.subscribe(WeakReference(this))
        onFeaturesUpdated()
    }

    fun toggleFeature(featureName: String) {
        settingsManager.toggleFeature(featureName)
    }

    override fun onFeaturesUpdated() {
        val arr = ArrayList<ToggleableFeature>()
        Features.availableFeatures.forEach {
            arr.add(
                ToggleableFeature(
                    it.second,
                    it.first,
                    settingsManager.isFeatureEnabled(it.second)
                )
            )
        }
        features.postValue(arr)
    }
}
