package com.nevmem.moneysaver.activity

import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.fragments.RegisterDialogChooseLoginFragment
import com.nevmem.moneysaver.fragments.RegisterDialogMainInfoFragment
import com.nevmem.moneysaver.fragments.RegisterDialogPasswordFragment
import kotlinx.android.synthetic.main.register_page.*

class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel: RegisterPageViewModel
    var index = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        window.enterTransition = Fade()
        window.exitTransition = Fade()
        viewModel = ViewModelProviders.of(this).get(RegisterPageViewModel::class.java)

        if (index == -1) {
            index = 0
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragmentsAnchor, dialogFragments[index])
            transaction.commit()
        }
    }

    private val dialogFragments = arrayListOf(
        RegisterDialogMainInfoFragment(),
        RegisterDialogChooseLoginFragment(),
        RegisterDialogPasswordFragment()
    )

    override fun onStart() {
        super.onStart()

        nextButton.setOnClickListener {
            next()
        }

        prevButton.setOnClickListener {
            prev()
        }
    }

    private fun switchFragmentLeft(index: Int) {
        with(supportFragmentManager.beginTransaction()) {
            setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
            replace(R.id.fragmentsAnchor, dialogFragments[index])
            commit()
        }
    }

    private fun switchFragmentRight(index: Int) {
        with(supportFragmentManager.beginTransaction()) {
            setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
            replace(R.id.fragmentsAnchor, dialogFragments[index])
            commit()
        }
    }

    private fun prev() {
        if (index >= 1) {
            index -= 1
            switchFragmentRight(index)
            nextButton.text = "Next"
        }
    }

    private fun proceedRegistration() {

    }

    private fun next() {
        if (index + 1 < dialogFragments.size) {
            index += 1
            switchFragmentLeft(index)
            if (index == dialogFragments.size - 1) {
                nextButton.text = "Register"
            }
        } else {
            proceedRegistration()
        }
    }
}
