package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.i
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.dagger.components.AppComponent
import com.nevmem.moneysaver.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.data.*
import com.nevmem.moneysaver.structure.Callback
import io.reactivex.subjects.BehaviorSubject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class App : Application() {
    lateinit var user: User
    var info: Info

    lateinit var requestQueue: RequestQueue

    var appComponent: AppComponent

    init {
        i("APP_CLASS", "App() init method was called")
        info = Info()

        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule(this))
            .networkModule(NetworkModule(this))
            .build()
    }

    private fun userCredentialsJSON(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }

    override fun onCreate() {
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
        i("APP_CLASS", "onCreate method was called")
    }

    private fun isValidUserInfo(json: JSONObject): Boolean {
        if (json.has("token") && json.has("first_name") &&
            json.has("last_name") && json.has("login")
        )
            return true
        return false
    }

    fun tryLogin(login: String, password: String, onSuccess: Callback<String>, onError: Callback<String>) {
        val params = JSONObject()
        params.put("login", login)
        params.put("password", password)
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiLogin,
            params,
            {
                System.out.println(it.toString())
                if (it.has("err")) {
                    onError.callback(it.getString("err"))
                } else {
                    if (!isValidUserInfo(it)) {
                        onError.callback("Wrong server response")
                    } else {
                        val parsedUser = User(
                            it.getString("login"), it.getString("token"),
                            it.getString("first_name"), it.getString("last_name")
                        )

                        User.saveUserCredentials(this, parsedUser)
                        user = parsedUser
                        onSuccess.callback(null)
                    }
                }
            }, {
                onError.callback(it.toString())
            })
        requestQueue.add(jsonRequest)
    }

    fun loadInfo(onSuccess: Callback<JSONObject>, onError: Callback<String>) {
        info = Info()
        System.out.println("Hello from info loader")
        val options = userCredentialsJSON()
        options.put("daysDescription", "true")
        options.put("info7", "true")
        options.put("info30", "true")
        options.put("months", "true")
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiInfo, options,
            {
                if (!it.has("type")) {
                    onError.callback("Server response has unknown format")
                } else {
                    if (it.getString("type") == "ok") {
                        info.fromJSON(it.getJSONObject("info"))
                        onSuccess.callback(it.getJSONObject("info"))
                    } else {
                        onError.callback("Server error")
                    }
                }
            }, {
                System.out.println(it.toString())
                onError.callback("Internet error")
            })
        requestQueue.add(jsonRequest)
    }
}
