package com.nevmem.moneysaver.app.activity

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.adapters.MainPageViewPager2Adapter
import com.nevmem.moneysaver.common.utils.ThreadUtils
import com.nevmem.moneysaver.ui.CustomSnackbar
import com.nevmem.moneysaver.ui.actions.ExtendedSnackbarAction
import com.nevmem.moneysaver.ui.actions.SnackbarAction
import com.nevmem.moneysaver.ui.useCases.SnackbarUseCase
import kotlinx.android.synthetic.main.main_page_layout.*
import javax.inject.Inject

class MainPage : AppCompatActivity(), SnackbarUseCase.Callback {
    companion object {
        const val BOTTOM_BAR_ANIMATION_DURATION = 300L
    }

    lateinit var app: App

    @Inject
    lateinit var snackbarUseCase: SnackbarUseCase

    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_left)
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)

        app = applicationContext as App
        app.appComponent.inject(this)

        val adapter = MainPageViewPager2Adapter(lifecycle, supportFragmentManager)
        anchor.adapter = adapter
        anchor.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> mainPageNavigation.selectedItemId = R.id.dashboardPageNavigation
                    1 -> mainPageNavigation.selectedItemId = R.id.templatesNavigation
                    2 -> mainPageNavigation.selectedItemId = R.id.newRecordNavigation
                    else -> mainPageNavigation.selectedItemId = R.id.historyNavigation
                }
            }
        })

        mainPageNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboardPageNavigation -> {
                    anchor.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.templatesNavigation -> {
                    anchor.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.newRecordNavigation -> {
                    anchor.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.historyNavigation -> {
                    anchor.currentItem = 3
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        anchor.isUserInputEnabled = false

        mainPageNavigation.selectedItemId = R.id.dashboardPageNavigation
    }

    override fun onResume() {
        super.onResume()
        snackbarUseCase.subscribe(this)
    }

    override fun onPause() {
        super.onPause()
        snackbarUseCase.unsubscribe(this)
    }

    override fun onActionReceived(action: SnackbarAction) {
        showSnackBar(action)
    }

    fun showSnackBar(action: SnackbarAction) {
        hideNavigationBar {
            showSnackbarWithCallback(action) {
                showNavigationBar()
            }
        }
    }

    private fun bottomBarHeight()
        = mainPageNavigation.height +
        (mainPageNavigation.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

    private fun showSnackbarWithCallback(action: SnackbarAction, onEnd: (() -> Unit)? = null) {
        val snack = CustomSnackbar.make(rootView, action.message, Snackbar.LENGTH_LONG)

        if (action is ExtendedSnackbarAction) {
            snack.setAction(action.buttonText) {
                action.callback()
            }
        }

        snack.addCallback(object : BaseTransientBottomBar.BaseCallback<CustomSnackbar>() {
            override fun onDismissed(transientBottomBar: CustomSnackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                onEnd?.invoke()
            }
        })
        snack.show()
    }

    private fun showNavigationBar(onEnd: (()->Unit)? = null) {
        ThreadUtils.runOnUi {
            animator?.cancel()
            val newAnimator = ValueAnimator.ofFloat(1f, 0f)
            newAnimator.duration = BOTTOM_BAR_ANIMATION_DURATION
            val startTranslationY = mainPageNavigation.translationY
            newAnimator.addUpdateListener {
                mainPageNavigation.translationY = it.animatedValue as Float * startTranslationY
            }
            newAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {}
                override fun onAnimationCancel(p0: Animator?) {
                    newAnimator.removeListener(this)
                }

                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    onEnd?.invoke()
                }
            })
            newAnimator.start()
            animator = newAnimator
        }
    }

    private fun hideNavigationBar(onEnd: (()->Unit)? = null) {
        ThreadUtils.runOnUi {
            animator?.cancel()
            val newAnimator = ValueAnimator.ofFloat(0f, 1f)
            newAnimator.duration = BOTTOM_BAR_ANIMATION_DURATION
            val startTranslation = mainPageNavigation.translationY
            newAnimator.addUpdateListener {
                mainPageNavigation.translationY =
                    startTranslation + it.animatedValue as Float * (bottomBarHeight() - startTranslation)
            }
            newAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {}
                override fun onAnimationCancel(p0: Animator?) {
                    newAnimator.removeListener(this)
                }

                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    onEnd?.invoke()
                }
            })
            newAnimator.start()
            animator = newAnimator
        }
    }

    private fun showDefaultToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    override fun onBackPressed() {
        showDefaultToast("Press logout button if you want get out from application")
    }
}