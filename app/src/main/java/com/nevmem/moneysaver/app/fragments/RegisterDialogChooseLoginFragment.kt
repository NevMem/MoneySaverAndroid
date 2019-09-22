package com.nevmem.moneysaver.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.data.RegistrationArgs
import com.nevmem.moneysaver.app.data.util.BadFilled
import com.nevmem.moneysaver.app.data.util.FillInfo
import com.nevmem.moneysaver.app.data.util.FilledWell
import com.nevmem.moneysaver.app.fragments.interfaces.Injector
import kotlinx.android.synthetic.main.register_dialog_choose_login.*

class RegisterDialogChooseLoginFragment : WellFilledCheckableFragment(), Injector<RegistrationArgs> {
    private lateinit var viewModel: RegisterDialogChooseLoginFragmentViewModel

    private var login: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_dialog_choose_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { itActivity ->
            run {
                viewModel = ViewModelProviders.of(itActivity).get(RegisterDialogChooseLoginFragmentViewModel::class.java)
                viewModel.loginChecking.observe(this, Observer {
                    when (it) {
                        null, RegisterDialogChooseLoginFragmentViewModel.Status.NONE -> {
                            loginField.loading = false
                            loginField.error = ""
                            loginField.success = false
                        }
                        RegisterDialogChooseLoginFragmentViewModel.Status.CHECKING -> {
                            loginField.loading = true
                        }
                        RegisterDialogChooseLoginFragmentViewModel.Status.SUCCESS -> {
                            login = loginField.text
                            loginField.success = true
                            loginField.error = ""
                            loginField.loading = false
                        }
                        RegisterDialogChooseLoginFragmentViewModel.Status.ERROR -> {
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
