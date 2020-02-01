package com.nevmem.moneysaver.app.activity.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.util.ErrorState
import com.nevmem.moneysaver.app.data.util.LoadingState
import com.nevmem.moneysaver.app.data.util.RequestState
import com.nevmem.moneysaver.app.data.util.SuccessState
import com.nevmem.moneysaver.auth.UserHolder
import org.json.JSONObject
import javax.inject.Inject

class LoginPageViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var networkQueue: com.nevmem.moneysaver.network.NetworkQueue

    @Inject
    lateinit var userHolder: UserHolder

    private val requestState = MutableLiveData<RequestState>()

    fun requestState(): LiveData<RequestState> = requestState

    init {
        application as App
        application.appComponent.inject(this)
    }

    enum class State {
        NONE,
        REQUESTING
    }

    private var currentState = State.NONE

    fun tryLogin(login: String, password: String) {
        val params = JSONObject()
        params.put("login", login)
        params.put("password", password)

        if (currentState == State.REQUESTING)
            return

        currentState = State.REQUESTING

        requestState.postValue(LoadingState)

        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiLogin, params, {
            if (it.has("type")) {
                when (it.getString("type")) {
                    "ok" -> {
                        val json = it.getJSONObject("data")
                        userHolder.initializeByJson(json)
                        requestState.postValue(SuccessState())
                    }
                    "error" -> requestState.postValue(ErrorState(it.getString("error")))
                    else -> requestState.postValue(ErrorState("Sever response has unknown format"))
                }
            } else {
                requestState.postValue(ErrorState("Sever response has unknown format"))
            }
            currentState = State.NONE
        })
    }
}