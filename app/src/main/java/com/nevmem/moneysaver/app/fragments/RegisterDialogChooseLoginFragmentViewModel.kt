package com.nevmem.moneysaver.app.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.network.Request
import com.nevmem.moneysaver.app.data.util.ParseError
import com.nevmem.moneysaver.app.data.util.ParseResult
import com.nevmem.moneysaver.app.data.util.ParsedValue
import org.json.JSONObject
import javax.inject.Inject

class RegisterDialogChooseLoginFragmentViewModel(app: Application) : AndroidViewModel(app) {
    enum class Status { CHECKING, SUCCESS, ERROR, NONE }

    val loginChecking = MutableLiveData<Status>(Status.NONE)
    private var loginCheckRequest: Request<JSONObject>? = null

    @Inject
    lateinit var networkQueue: com.nevmem.moneysaver.network.NetworkQueue

    init {
        (app as App).appComponent.inject(this)
    }

    private fun parseCheckLoginRequest(json: JSONObject): ParseResult {
        val type = json.optString("type")
        if (type == null || (type != "error" && type != "ok"))
            return ParseError("Unknown server response format")
        if (type == "error") {
            val error = json.optString("error") ?: return ParseError("Unknown server response error")
            return ParseError(error)
        }
        val result = json.optString("result") ?: return ParseError("Unknown server response error")
        if (result == "ok")
            return ParsedValue(true)
        return ParsedValue(false)
    }

    fun checkLogin(login: String) {
        if (login.isEmpty()) {
            loginCheckRequest?.cancel()
            loginChecking.postValue(Status.NONE)
        } else {
            loginCheckRequest?.cancel()
            val params = JSONObject()
            params.put("login", login)
            loginChecking.postValue(Status.CHECKING)
            loginCheckRequest = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiCheckLogin, params)
            loginCheckRequest?.success {
                val parse = parseCheckLoginRequest(it)
                loginChecking.postValue(Status.NONE)
                if (parse is ParseError) {
                    loginChecking.postValue(Status.ERROR)
                } else if (parse is ParsedValue<*>) {
                    val value = parse.parsed as? Boolean
                    if (value == null || value == false) {
                        loginChecking.postValue(Status.ERROR)
                    } else {
                        loginChecking.postValue(Status.SUCCESS)
                    }
                }
            }
        }
    }
}