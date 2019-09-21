package com.nevmem.moneysaver.app.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.register_main_info.*

class RegisterDialogMainInfoFragment : WellFilledCheckableFragment(), Injector<RegistrationArgs> {
    private var firstName: String? = null
    private var lastName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_main_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (activity as RegisterActivity).viewModel

        nameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                firstName = s?.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        surnameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                lastName = s?.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun inject(objectInjectTo: RegistrationArgs) {
        if (firstName != null)
            objectInjectTo.firstName = firstName
        if (lastName != null)
            objectInjectTo.lastName = lastName
    }

    override fun isWellFilled(): FillInfo {
        firstName.let {
            if (it == null || it.isEmpty()) return BadFilled("Name cannot be empty")
        }
        lastName.let {
            if (it == null || it.isEmpty()) return BadFilled("Surname cannot be empty")
        }
        return FilledWell
    }
}