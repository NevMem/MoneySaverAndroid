package com.nevmem.moneysaver.app.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.app.activity.viewModels.LoginPageViewModel
import com.nevmem.moneysaver.app.data.UserHolder
import com.nevmem.moneysaver.app.views.InfoDialog
import kotlinx.android.synthetic.main.login_page.*
import org.json.JSONObject
import javax.inject.Inject

class LoginPageActivity : AppCompatActivity() {
    private lateinit var loginModel: LoginPageViewModel

    @Inject
    lateinit var networkQueue: com.nevmem.moneysaver.network.NetworkQueue

    @Inject
    lateinit var userHolder: UserHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        window.exitTransition = Fade()
        window.enterTransition = Fade()

        loginModel = ViewModelProviders.of(this).get(LoginPageViewModel::class.java)

        loginModel.error.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                showError(it)
            }
        })
        loginModel.loading.observe(this, Observer {
            if (it == true)
                loginPageLoadingBar.visibility = View.VISIBLE
            else
                loginPageLoadingBar.visibility = View.INVISIBLE
        })

        (applicationContext as App).appComponent.inject(this)

        registerButton.setOnClickListener {
            openRegisterPage()
        }
    }

    private fun showError(error: String) {
        val dialog = InfoDialog("Error happened", error)
        dialog.show(supportFragmentManager, "error_info_dialog")
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).post {
            if (userHolder.ready) {
                goToHomePage()
            }
        }
    }

    private fun openRegisterPage() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun goToHomePage() {
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
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
                when (it.getString("type")) {
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
