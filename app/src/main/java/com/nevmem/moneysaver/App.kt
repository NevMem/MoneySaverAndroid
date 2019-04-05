package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.i
import com.nevmem.moneysaver.dagger.components.AppComponent
import com.nevmem.moneysaver.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.data.Info
import org.json.JSONObject

class App : Application() {
    var info: Info

    var appComponent: AppComponent

    init {
        i("APP_CLASS", "App() init method was called")
        info = Info()

        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule(this))
            .networkModule(NetworkModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        i("APP_CLASS", "onCreate method was called")
    }

    private fun isValidUserInfo(json: JSONObject): Boolean {
        if (json.has("token") && json.has("first_name") &&
            json.has("last_name") && json.has("login")
        )
            return true
        return false
    }

    /* fun tryLogin(login: String, password: String, onSuccess: Callback<String>, onError: Callback<String>) {
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
    } */
}
