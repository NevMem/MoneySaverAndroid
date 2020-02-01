package com.nevmem.moneysaver.app.activity

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.viewModels.DevSettingsPageViewModel
import com.nevmem.moneysaver.app.utils.UnitsHelper
import kotlinx.android.synthetic.main.dev_settings_page.*

class DevSettingsActivity : FragmentActivity() {
    lateinit var viewModel: DevSettingsPageViewModel

    companion object {
        const val DEFAULT_DRAWABLE_SIZE = 24f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dev_settings_page)
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_left)

        viewModel = ViewModelProviders.of(this).get(DevSettingsPageViewModel::class.java)

        viewModel.features().observe(this, Observer {
            if (it != null) {
                featuresAnchor.removeAllViews()

                it.forEach { featureIt -> run {
                    val view = createFeatureView(featureIt.description, featureIt.isEnabled)
                    view.setOnClickListener {
                        Toast.makeText(this, "Reset app to apply changes", Toast.LENGTH_LONG).show()
                        viewModel.toggleFeature(featureIt.featureName)
                    }
                    featuresAnchor.addView(view)
                }}
            }
        })
    }

    private fun createFeatureView(featureName: String, isActive: Boolean): View {
        val textView = TextView(this)
        textView.text = featureName

        val verticalPadding = UnitsHelper.fromDp(16f, resources.displayMetrics).toInt()
        val horizontalPadding = 0

        textView.setPadding(
            horizontalPadding,
            verticalPadding,
            horizontalPadding,
            verticalPadding)

        if (isActive) {
            val drawable = resources.getDrawable(R.drawable.check_icon_white, theme)
            val size = UnitsHelper.fromDp(DEFAULT_DRAWABLE_SIZE, resources.displayMetrics).toInt()
            drawable.setBounds(0, 0, size, size)
            textView.setCompoundDrawables(null, null, drawable, null)
        }

        return textView
    }
}
