package com.nevmem.moneysaver.app.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.viewModels.RegisterPageViewModel
import com.nevmem.moneysaver.app.data.RegistrationArgs
import com.nevmem.moneysaver.auth.UserHolder
import com.nevmem.moneysaver.app.data.util.BadFilled
import com.nevmem.moneysaver.app.data.util.FilledWell
import com.nevmem.moneysaver.app.fragments.*
import com.nevmem.moneysaver.app.fragments.interfaces.Injector
import com.nevmem.moneysaver.app.views.LoadingOverlay
import kotlinx.android.synthetic.main.register_page.*
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel: RegisterPageViewModel
    var index = -1

    private var overlay: LoadingOverlay? = null
    private var popup: PopupWindow? = null

    @Inject
    lateinit var userHolder: UserHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        window.enterTransition = Fade()
        window.exitTransition = Fade()
        viewModel = ViewModelProviders.of(this).get(RegisterPageViewModel::class.java)

        (applicationContext as App).appComponent.inject(this)

        if (index == -1) {
            index = 0
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragmentsAnchor, dialogFragments[index])
            transaction.commit()
        }

        viewModel.loading.observe(this, Observer {
            if (it != null && it) {
                showLoading()
            }
        })

        viewModel.registerSuccess.observe(this, Observer {
            if (it != null) {
                showSuccess(it)
            }
        })

        viewModel.registerError.observe(this, Observer {
            if (it != null) {
                showError(it)
            }
        })
    }

    private val dialogFragments = arrayListOf(
        RegisterDialogMainInfoFragment(),
        RegisterDialogChooseLoginFragment(),
        RegisterDialogPasswordFragment(),
        RegisterDialogPrivacyFragment()
    )

    private fun currentDialog() = dialogFragments[index]


    private fun tryGoToHomePage() {
        checkNotNull(viewModel.user) { "User in view model cannot be null after on success calling" }
        val curUser = viewModel.user
        if (curUser != null)
            userHolder.initializeByUser(curUser)
        if (userHolder.ready) {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }


    private fun initOverlay() {
        if (overlay == null) {
            overlay = LoadingOverlay(this)
        }
        if (appName != null) {
            popup = PopupWindow(overlay, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            popup?.showAtLocation(appName, Gravity.CENTER, 0, 0)
            overlay?.let { overlayIt ->
                overlayIt.setAfterSuccess {
                    disposeOverlay()
                    tryGoToHomePage()
                }

                overlayIt.setAfterError {
                    disposeOverlay()
                }

                overlayIt.setOnInterrupt {
                    disposeOverlay()
                }
            }
        }
    }

    private fun disposeOverlay() {
        if (popup != null) popup?.dismiss()
        popup = null
        overlay = null
    }

    private fun showLoading() {
        if (popup == null) initOverlay()
        overlay?.apply { setLoading("Loading") }
    }

    private fun showError(error: String) {
        if (popup == null) initOverlay()
        overlay.let { it?.setError(error) }
    }

    private fun showSuccess(success: String) {
        if (popup == null) initOverlay()
        overlay.let { it?.setSuccess(success) }
    }

    override fun onStart() {
        super.onStart()

        nextButton.setOnClickListener {
            val filled = currentDialog().isWellFilled()
            if (filled is FilledWell)
                next()
            else if (filled is BadFilled)
                showError(filled.reason)
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
            nextButton.text = getString(R.string.next_button_string)
        }
    }

    private fun proceedRegistration() {
        for (i in 0 until (dialogFragments.size)) {
            val current = dialogFragments[i].isWellFilled()
            if (current is BadFilled) {
                showError(current.reason)
                return
            }
            if (dialogFragments[i] is Injector<*>)
                (dialogFragments[i] as? Injector<RegistrationArgs>)?.inject(viewModel.registrationArgs)
        }
        viewModel.register()
    }

    private fun next() {
        if (index + 1 < dialogFragments.size) {
            index += 1
            switchFragmentLeft(index)
            if (index == dialogFragments.size - 1) {
                nextButton.text = getString(R.string.register)
            }
        } else {
            proceedRegistration()
        }
    }

    override fun onStop() {
        if (popup != null) popup?.dismiss()
        super.onStop()
    }
}
