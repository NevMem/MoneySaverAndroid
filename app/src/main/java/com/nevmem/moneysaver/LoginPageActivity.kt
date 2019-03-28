package com.nevmem.moneysaver

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.login_page.*

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
        val intent = Intent(this, MainPage::class.java)
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
