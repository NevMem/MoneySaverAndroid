package com.nevmem.moneysaver

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.UserHolder
import kotlinx.android.synthetic.main.login_page.*
import org.json.JSONObject
import javax.inject.Inject

class LoginPageActivity : FragmentActivity() {
    private lateinit var loginModel: LoginPageViewModel

    @Inject
    lateinit var networkQueue: NetworkQueueBase

    @Inject
    lateinit var userHolder: UserHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.mPurple)

        loginModel = ViewModelProviders.of(this).get(LoginPageViewModel::class.java)

        loginModel.error.observe(this, Observer {
            errors.text = it
        })
        loginModel.loading.observe(this, Observer {
            if (it == true)
                loginPageLoadingBar.visibility = View.VISIBLE
            else
                loginPageLoadingBar.visibility = View.INVISIBLE
        })

        (applicationContext as App).appComponent.inject(this)
        if (userHolder.ready) {
            goToHomePage()
        }
    }

    private fun goToHomePage() {
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent)
    }

    fun onLoginButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val login = loginField.text.toString()
        val password = passwordField.text.toString()
        val params = JSONObject()
        params.put("login", login)
        params.put("password", password)

        loginModel.error.postValue("")
        loginModel.loading.postValue(true)

        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiLogin, params, {
            loginModel.loading.postValue(false)
            if (it.has("type")) {
                val type = it.getString("type")
                when (type) {
                    "ok" -> {
                        val json = it.getJSONObject("data")
                        userHolder.initializeByJson(json)
                        goToHomePage()
                    }
                    "error" -> loginModel.error.postValue(it.getString("error"))
                    else -> loginModel.error.postValue("Sever response has unknown format")
                }
            } else {
                loginModel.error.postValue("Sever response has unknown format")
            }
        })
    }
}
