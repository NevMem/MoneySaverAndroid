package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.RegisterActivity
import com.nevmem.moneysaver.activity.RegisterPageViewModel
import com.nevmem.moneysaver.data.RegistrationArgs
import com.nevmem.moneysaver.data.util.BadFilled
import com.nevmem.moneysaver.data.util.FillInfo
import com.nevmem.moneysaver.data.util.FilledWell
import com.nevmem.moneysaver.fragments.interfaces.Injecter
import kotlinx.android.synthetic.main.register_dialog_choose_login.*

class RegisterDialogChooseLoginFragment : WellFilledCheckableFragment(), Injecter<RegistrationArgs> {
    lateinit var viewModel: RegisterPageViewModel

    private var login: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_dialog_choose_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            with(it as RegisterActivity) {
                this@RegisterDialogChooseLoginFragment.viewModel = viewModel
            }
        }
        viewModel.loginChecking.observe(this, Observer {
            when (it) {
                null, RegisterPageViewModel.Status.NONE -> {
                    loginField.loading = false
                    loginField.error = ""
                    loginField.success = false
                }
                RegisterPageViewModel.Status.CHECKING -> {
                    loginField.loading = true
                }
                RegisterPageViewModel.Status.SUCCESS -> {
                    login = loginField.text
                    loginField.success = true
                    loginField.error = ""
                    loginField.loading = false
                }
                RegisterPageViewModel.Status.ERROR -> {
                    loginField.error = "Error happened"
                    loginField.loading = false
                    loginField.success = false
                }
            }
        })

        loginField.changeHandler = {
            viewModel.checkLogin(it)
        }
    }

    override fun inject(objectInjectTo: RegistrationArgs) {
        if (login != null)
            objectInjectTo.login = login
    }

    override fun isWellFilled(): FillInfo {
        login.let {
            if (it == null || it.isEmpty()) return BadFilled("Login cannot be empty")
            return FilledWell
        }
    }
}
