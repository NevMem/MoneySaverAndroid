package com.nevmem.moneysaver

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView

import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_page.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class LoginPageActivity : FragmentActivity() {

    private lateinit var loginModel: LoginPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        loginModel = ViewModelProviders.of(this).get(LoginPageViewModel::class.java)

        loginModel.error.observe(this, object: Observer<String> {
            override fun onChanged(value: String?) {
                errors.text = value
            }
        })

        try {
            User.loadUserCredintials(this)
            goToHomePage()
        } catch (e: UserCredentialsNotFound) {
            System.out.println("There is no user credentials")
        }
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

        val jsonRequest = JsonObjectRequest(Request.Method.POST, "http://104.236.71.129/api/login",
            params, {
                if (it.has("err")) {
                    loginModel.error.value = it.getString("err")
                } else {
                    if (isValidUserInfo(it)) {
                        val user = User(it.getString("login"), it.getString("token"),
                            it.getString("first_name"), it.getString("last_name"))

                        User.saveUserCredentials(this, user)
                    } else {
                        loginModel.error.value = "Server response was incorrect"
                    }
                }
        }, {
            loginModel.error.value = it.toString()
        })

        Volley.newRequestQueue(this).add(jsonRequest)
    }
}
