package com.nevmem.moneysaver.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.RegisterActivity
import com.nevmem.moneysaver.app.data.RegistrationArgs
import com.nevmem.moneysaver.app.data.util.BadFilled
import com.nevmem.moneysaver.app.data.util.FillInfo
import com.nevmem.moneysaver.app.data.util.FilledWell
import com.nevmem.moneysaver.app.fragments.interfaces.Injector
import kotlinx.android.synthetic.main.register_dialog_password.*

class RegisterDialogPasswordFragment : WellFilledCheckableFragment(), Injector<RegistrationArgs> {
    private var chosenPassword: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_dialog_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (activity as RegisterActivity).viewModel
        passwordField.changeHandler = {
            when {
                it.length >= 6 -> {
                    passwordField.success = true
                    passwordField.error = ""
                }
                it.isNotEmpty() -> {
                    passwordField.success = false
                    passwordField.error = "Password is too short"
                }
                else -> {
                    passwordField.success = false
                    passwordField.error = ""
                }
            }
            if (it != confirmPasswordField.text) {
                confirmPasswordField.success = false
                confirmPasswordField.error = "Passwords are not equal"
            }
        }
        confirmPasswordField.changeHandler = {
            if (it == passwordField.text && it.length >= 6) {
                chosenPassword = it
                confirmPasswordField.success = true
                confirmPasswordField.error = ""
            } else if (it.isNotEmpty()) {
                confirmPasswordField.error = "Passwords are not equal"
                confirmPasswordField.success = false
            } else {
                confirmPasswordField.error = ""
                confirmPasswordField.success = false
            }
        }
    }

    override fun inject(objectInjectTo: RegistrationArgs) {
        if (chosenPassword != null) {
            objectInjectTo.chosenPassword = chosenPassword
        }
    }

    override fun isWellFilled(): FillInfo {
        chosenPassword.let {
            if (it == null || it.isEmpty()) return BadFilled("Password is incorrect")
        }
        return FilledWell
    }
}