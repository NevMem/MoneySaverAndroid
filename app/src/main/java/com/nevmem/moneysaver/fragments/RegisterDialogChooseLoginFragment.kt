package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
            with (it as RegisterActivity) {
                this@RegisterDialogChooseLoginFragment.viewModel = viewModel
            }
        }
        viewModel.loginChecking.observe(this, Observer {
            checkingLogin.visibility = View.GONE
            successIcon.visibility = View.GONE
            errorIcon.visibility = View.GONE
            when (it) {
                null, RegisterPageViewModel.Status.NONE -> {}
                RegisterPageViewModel.Status.CHECKING -> {
                    checkingLogin.visibility = View.VISIBLE
                }
                RegisterPageViewModel.Status.SUCCESS -> {
                    successIcon.visibility = View.VISIBLE
                }
                RegisterPageViewModel.Status.ERROR -> {
                    errorIcon.visibility = View.VISIBLE
                }
            }
        })

        loginField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.checkLogin(loginField.text.toString())
            }
        })
    }
}