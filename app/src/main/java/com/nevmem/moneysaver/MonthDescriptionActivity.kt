package com.nevmem.moneysaver

import android.graphics.Color
import android.os.Bundle
import android.transition.Explode
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.data.repositories.InfoRepository
import kotlinx.android.synthetic.main.month_description_page.*
import javax.inject.Inject

class MonthDescriptionActivity : AppCompatActivity() {
    @Inject
    lateinit var infoRepo: InfoRepository

    private var colors = arrayListOf(
        Color.parseColor("#03f7eb"),
        Color.parseColor("#18ff6d"),
        Color.parseColor("#ff9505"),
        Color.parseColor("#5b2a86"),
        Color.parseColor("#f2545b"),
        Color.parseColor("#fffd82"),
        Color.parseColor("#a9e5bb"),
        Color.parseColor("#eee82c"),
        Color.parseColor("#32908f"),
        Color.parseColor("#26c485"),
        Color.parseColor("#ff6542"),
        Color.parseColor("#7d8cc4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month_description_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)

        (applicationContext as App).appComponent.inject(this)

        headerText.text = "Last month description"

        window.sharedElementEnterTransition.duration = 200
        window.enterTransition = Explode()

        infoRepo.lastMonthDescription.observe(this, Observer {
            if (it != null) {
                val labels = ArrayList<String>()
                val values = ArrayList<Double>()

                for (key in it.byTagTotal.keys) {
                    labels.add(key)
                    values.add(it.byTagTotal[key]!!)
                }
                chart.setData(values, colors)
            }
        })
    }
}