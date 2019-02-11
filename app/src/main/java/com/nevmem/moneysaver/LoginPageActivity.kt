package com.nevmem.moneysaver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.*

import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.login_page.*
import org.json.JSONObject

class LoginPageActivity : FragmentActivity() {

    private lateinit var loginModel: LoginPageViewModel

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

        try {
            User.loadUserCredintials(this)
            saveUserToApplication()
            goToHomePage()
        } catch (e: UserCredentialsNotFound) {
            System.out.println("There is no user credentials")
        }
    }

    private fun saveUserToApplication() {
        val savedUser = User.loadUserCredintials(this)
        val application = applicationContext as App
        application.user = savedUser
        application.loadAll()
    }

    fun goToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
    }

    fun onLoginButtonClick(view: View) {
        val login = loginField.text.toString()
        val password = passwordField.text.toString()

        loginModel.error.value = ""
        loginModel.loading.value = true

        val app = applicationContext as App
        app.tryLogin(login, password, Callback {
            loginModel.loading.value = false
            goToHomePage()
        }, Callback {
            loginModel.loading.value = false
            loginModel.error.value = it!!.toString()
        })
    }
}
