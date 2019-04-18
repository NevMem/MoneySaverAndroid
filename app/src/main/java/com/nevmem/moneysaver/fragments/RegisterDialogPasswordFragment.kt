package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.register_dialog_password.*

class RegisterDialogPasswordFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_dialog_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        }
        confirmPasswordField.changeHandler = {
            if (it == passwordField.text && it.length >= 6) {
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
}