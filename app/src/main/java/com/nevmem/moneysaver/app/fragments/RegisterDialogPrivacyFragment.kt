package com.nevmem.moneysaver.app.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.data.util.*
import kotlinx.android.synthetic.main.register_dialog_privacy.*
import kotlinx.android.synthetic.main.register_dialog_privacy.view.*

class RegisterDialogPrivacyFragment : WellFilledCheckableFragment() {
    private lateinit var viewModel: RegisterDialogPrivacyFragmentViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            viewModel = ViewModelProviders.of(this).get(RegisterDialogPrivacyFragmentViewModel::class.java)
            setupViewModelObservers()
        }
    }

    private fun showLoading() {
        textView.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun showText(text: String) {
        loading.visibility = View.GONE
        textView.text = text
        textView.visibility = View.VISIBLE
    }

    private fun setupViewModelObservers() {
        viewModel.state.observe(this, Observer {
            when (it) {
                null, NoneState -> {
                    checkBox.isChecked = false
                    checkBox.isEnabled = false
                    checkBox.invalidate()
                }
                LoadingState -> {
                    checkBox.isChecked = false
                    checkBox.isEnabled = false
                    showLoading()
                }
                is SuccessState -> {
                    checkBox.isEnabled = true
                    if (it.success != null)
                        showText(it.success)
                }
                is ErrorState -> {
                    checkBox.isChecked = false
                    checkBox.isEnabled = false
                    Toast.makeText(activity, "Error while loading privacy convention", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.register_dialog_privacy, container, false).also {
            it.checkBoxLabel.setOnClickListener { _ ->
                it.checkBox.isChecked = !checkBox.isChecked
            }
        }
    }

    override fun isWellFilled(): FillInfo {
        if (!checkBox.isChecked)
            return BadFilled("You have to accept privacy convention")
        return FilledWell
    }
}
