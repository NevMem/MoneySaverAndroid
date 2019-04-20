package com.nevmem.moneysaver.activity

import android.app.Application
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.RequestBase
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.data.util.HistoryRepositoryParsers.Companion.unknownFormat
import com.nevmem.moneysaver.data.util.ParseError
import com.nevmem.moneysaver.data.util.ParseResult
import com.nevmem.moneysaver.data.util.ParsedValue
import org.json.JSONObject
import java.lang.ClassCastException
import javax.inject.Inject

class RegisterPageViewModel(private var app: Application) : AndroidViewModel(app) {
    val tag: String = "REG_PAGE_VIEW_MODEL"

    enum class Status { CHECKING, SUCCESS, ERROR, NONE }

    val loginChecking = MutableLiveData<Status>(Status.NONE)
    val loading = MutableLiveData<Boolean>(false)

    var registerError = MutableLiveData<String?>()
    var registerSuccess = MutableLiveData<String?>()

    var chosenPassword: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var login: String? = null

    var user: User?= null

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

    private fun parseRegisterResponse(json: JSONObject): ParseResult {
        val type = json.optString("type") ?: return ParseError(unknownFormat)
        if (type == "error") {
            val error = json.optString("error") ?: return ParseError(unknownFormat)
            return ParseError(error)
        }
        val resp = json.optJSONObject("data") ?: return ParseError(unknownFormat)
        val token = resp.optString("token") ?: return ParseError(unknownFormat)
        val login = resp.optString("login") ?: return ParseError(unknownFormat)
        val firstName = resp.optString("first_name") ?: return ParseError(unknownFormat)
        val lastName = resp.optString("last_name") ?: return ParseError(unknownFormat)
        return ParsedValue(User(login, token, firstName, lastName))
    }

    private fun postError(error: String) {
        registerError.postValue(error)
    }

    private fun postSuccess(user: User) {
        this.user = user
        registerSuccess.postValue("Successfully registered")
    }

    fun register() {
        val params = JSONObject()
        params.put("login", login)
        params.put("firstName", firstName)
        params.put("lastName", lastName)
        params.put("password", chosenPassword)
        loading.postValue(true)
        registerError.postValue(null)
        registerSuccess.postValue(null)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiRegister, params)
        request.success {
            when (val parsed = parseRegisterResponse(it)) {
                is ParseError -> {
                    postError(parsed.reason)
                }
                is ParsedValue<*> -> {
                    try {
                        postSuccess(parsed.parsed as User)
                    } catch (e: ClassCastException) {
                        d("REG page VM", e.message)
                        postError("Bad server response")
                    }
                }
            }
        }
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