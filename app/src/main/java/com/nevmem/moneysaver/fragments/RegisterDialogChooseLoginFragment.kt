package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.RegisterActivity
import com.nevmem.moneysaver.activity.RegisterPageViewModel
import kotlinx.android.synthetic.main.register_dialog_choose_login.*

class RegisterDialogChooseLoginFragment : Fragment() {
    lateinit var viewModel: RegisterPageViewModel
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
                    viewModel.login = loginField.text
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
}