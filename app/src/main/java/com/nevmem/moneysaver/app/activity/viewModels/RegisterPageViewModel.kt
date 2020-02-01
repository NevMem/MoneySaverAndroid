package com.nevmem.moneysaver.app.activity.viewModels

import android.app.Application
import android.util.Log.d
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.common.Vars.Companion.unknownFormat
import com.nevmem.moneysaver.app.data.RegistrationArgs
import com.nevmem.moneysaver.app.data.util.ParseError
import com.nevmem.moneysaver.app.data.util.ParseResult
import com.nevmem.moneysaver.app.data.util.ParsedValue
import org.json.JSONObject
import javax.inject.Inject

class RegisterPageViewModel(app: Application) : AndroidViewModel(app) {
    val logTag: String = "REG_PAGE_VIEW_MODEL"

    val loading = MutableLiveData<Boolean>(false)

    var registerError = MutableLiveData<String?>()
    var registerSuccess = MutableLiveData<String?>()

    val registrationArgs = RegistrationArgs()

    var user: com.nevmem.moneysaver.auth.User?= null

    @Inject
    lateinit var networkQueue: com.nevmem.moneysaver.network.NetworkQueue

    init {
        (app as App).appComponent.inject(this)
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
        return ParsedValue(com.nevmem.moneysaver.auth.User(login, token, firstName, lastName))
    }

    private fun postError(error: String) {
        registerError.postValue(error)
    }

    private fun postSuccess(user: com.nevmem.moneysaver.auth.User) {
        this.user = user
        registerSuccess.postValue("Successfully registered")
    }

    fun register() {
        val params = JSONObject()
        params.put("login", registrationArgs.login)
        params.put("firstName", registrationArgs.firstName)
        params.put("lastName", registrationArgs.lastName)
        params.put("password", registrationArgs.chosenPassword)
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
                        postSuccess(parsed.parsed as com.nevmem.moneysaver.auth.User)
                    } catch (e: ClassCastException) {
                        d(logTag, "REG page VM ${e.message}")
                        postError("Bad server response")
                    }
                }
            }
        }
    }
}
