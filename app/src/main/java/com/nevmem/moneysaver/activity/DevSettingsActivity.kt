package com.nevmem.moneysaver.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.utils.UnitsHelper
import kotlinx.android.synthetic.main.dev_settings_page.*

class DevSettingsActivity : FragmentActivity() {
    lateinit var viewModel: DevSettingsPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dev_settings_page)

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

        textView.setPadding(0,
            UnitsHelper.fromDp(16f, resources.displayMetrics).toInt(),
            0,
            UnitsHelper.fromDp(16f, resources.displayMetrics).toInt())

        if (isActive) {
            val drawable = resources.getDrawable(R.drawable.check_icon_white)
            val size = UnitsHelper.fromDp(24f, resources.displayMetrics).toInt()
            drawable.setBounds(0, 0, size, size)
            textView.setCompoundDrawables(null, null, drawable, null)
        }

        return textView
    }
}