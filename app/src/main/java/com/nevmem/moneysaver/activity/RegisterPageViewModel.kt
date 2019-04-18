package com.nevmem.moneysaver.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.RequestBase
import com.nevmem.moneysaver.data.util.ParseError
import com.nevmem.moneysaver.data.util.ParseResult
import com.nevmem.moneysaver.data.util.ParsedValue
import org.json.JSONObject
import javax.inject.Inject

class RegisterPageViewModel(app: Application) : AndroidViewModel(app) {
    val tag: String = "REG_PAGE_VIEW_MODEL"

    enum class Status { CHECKING, SUCCESS, ERROR, NONE }

    val loginChecking = MutableLiveData<Status>(Status.NONE)
    val sending = MutableLiveData<Boolean>(false)

    var choosedPassword: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var login: String? = null

    @Inject
    lateinit var networkQueue: NetworkQueueBase

    private var loginCheckRequest: RequestBase<JSONObject>? = null

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