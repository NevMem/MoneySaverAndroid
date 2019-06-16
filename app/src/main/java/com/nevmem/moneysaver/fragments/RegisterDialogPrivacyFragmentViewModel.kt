package com.nevmem.moneysaver.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.util.LoadingState
import com.nevmem.moneysaver.data.util.RequestState
import com.nevmem.moneysaver.data.util.SuccessState
import org.json.JSONObject
import javax.inject.Inject

class RegisterDialogPrivacyFragmentViewModel(app: Application) : AndroidViewModel(app) {
    @Inject
    lateinit var networkQueue: NetworkQueueBase

    val state: MutableLiveData<RequestState> = MutableLiveData(LoadingState)

    init {
        (app as App).appComponent.inject(this)
        loadPrivacy()
    }

    private fun loadPrivacy() {
        val request = networkQueue.infinitePostStringRequest(Vars.ServerPrivacy, JSONObject())
        request.success {
            state.postValue(SuccessState(it))
        }
    }
}