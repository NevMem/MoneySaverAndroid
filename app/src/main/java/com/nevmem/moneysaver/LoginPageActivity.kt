package com.nevmem.moneysaver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.*

import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import kotlinx.android.synthetic.main.login_page.*
import org.json.JSONObject

class LoginPageActivity : FragmentActivity() {

    private lateinit var loginModel: LoginPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

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
    }

    fun goToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
    }

    fun isValidUserInfo(json: JSONObject): Boolean {
        if (json.has("token") && json.has("first_name") &&
                json.has("last_name") && json.has("login"))
            return true
        return false
    }

    fun onLoginButtonClick(view: View) {
        val login = loginField.text.toString()
        val password = passwordField.text.toString()

        val params = JSONObject()
        params.put("login", login)
        params.put("password", password)

        loginModel.error.value = ""
        loginModel.loading.value = true

        val jsonRequest = JsonObjectRequest(Request.Method.POST, "http://104.236.71.129/api/login",
            params, {
                loginModel.loading.value = false
                if (it.has("err")) {
                    loginModel.error.value = it.getString("err")
                } else {
                    if (isValidUserInfo(it)) {
                        val user = User(it.getString("login"), it.getString("token"),
                            it.getString("first_name"), it.getString("last_name"))

                        User.saveUserCredentials(this, user)
                        saveUserToApplication()
                        goToHomePage()
                    } else {
                        loginModel.error.value = "Server response was incorrect"
                    }
                }
        }, {
            loginModel.loading.value = false
            loginModel.error.value = it.toString()
        })

        Volley.newRequestQueue(this).add(jsonRequest)
    }
}
