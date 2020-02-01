package com.nevmem.moneysaver.app.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.viewModels.LoginPageViewModel
import com.nevmem.moneysaver.app.data.util.ErrorState
import com.nevmem.moneysaver.app.data.util.LoadingState
import com.nevmem.moneysaver.app.data.util.SuccessState
import com.nevmem.moneysaver.app.views.InfoDialog
import kotlinx.android.synthetic.main.login_page.*

class LoginPageActivity : AppCompatActivity() {
    private lateinit var loginModel: LoginPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        window.exitTransition = Fade()
        window.enterTransition = Fade()

        loginModel = ViewModelProviders.of(this).get(LoginPageViewModel::class.java)

        loginModel.requestState().observe(this, Observer {
            loginPageLoadingBar.visibility = View.GONE
            when (it) {
                is SuccessState -> {
                    goToHomePage()
                }
                is LoadingState -> {
                    loginPageLoadingBar.visibility = View.VISIBLE
                }
                is ErrorState -> {
                    showError(it.error)
                }
            }
        })

        loginButton.setOnClickListener {
            loginButtonClicked()
        }

        registerButton.setOnClickListener {
            openRegisterPage()
        }
    }

    private fun showError(error: String) {
        val dialog = InfoDialog("Error happened", error)
        dialog.show(supportFragmentManager, "error_info_dialog")
    }

    private fun openRegisterPage() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun goToHomePage() {
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun loginButtonClicked() {
        val login = loginField.text.toString()
        val password = passwordField.text.toString()

        loginModel.tryLogin(login, password)
    }
}
