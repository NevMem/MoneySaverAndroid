package com.nevmem.moneysaver

import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.ShowableListMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.record_layout.view.*
import kotlinx.android.synthetic.main.user_profile.*
import kotlinx.android.synthetic.main.home_page_activity.*
import android.util.Pair
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*

class HomePageActivity(var showedRecords: Int = 0) : AppCompatActivity() {

    lateinit var homeModel: HomePageActivityViewModel
    val DEFAULT_COUNT_OF_ELEMENTS = 20
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)

        app = applicationContext as App

        homeModel = ViewModelProviders.of(this).get(HomePageActivityViewModel::class.java)

        try {
            homeModel.user.value = User.loadUserCredintials(this)
        } catch (e: UserCredentialsNotFound) {
            System.out.println("User credentials not found")
        }

        homeModel.user.observe(this, Observer {
            userName.text = it!!.first_name
        })
        homeModel.averageSpend.observe(this, Observer {
            try {
                averageSpend.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                averageSpend.text = ""
            }
        })
        homeModel.totalSpend.observe(this, Observer {
            try {
                totalSpend.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                totalSpend.text = ""
            }
        })
        homeModel.amountOfDays.observe(this, Observer {
            try {
                trackedDays.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                trackedDays.text = ""
            }
        })
        homeModel.average7Days.observe(this, Observer {
            try {
                average7Days.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                average7Days.text = ""
            }
        })
        homeModel.average30Days.observe(this, Observer {
            try {
                average30Days.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                average30Days.text = ""
            }
        })
        homeModel.loading.observe(this, Observer {
            if (it == true)
                loadingBar.visibility = View.VISIBLE
            else
                loadingBar.visibility = View.GONE
        })
        homeModel.sumDay.observe(this, Observer {
            sumDayChart.values = it!!
            val animator = ValueAnimator.ofFloat(0f,  1f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener {
                sumDayChart.multiplier = it.animatedValue as Float
                sumDayChart.invalidate()
            }
            animator.duration = 500
            animator.start()
        })

        homePage.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY -> run{ // TODO:for endless scrolling
            if (topLinear.height - 200 <= scrollY + homePage.height) {
                hideAddButton()
            } else {
                showAddButton()
            }
        } }

        tryLoad()
    }

    private fun hideAddButton() {
        if (!homeModel.changingAddButtonState.value!!) {
            homeModel.changingAddButtonState.value = true
            val animator = addRecordButton.animate().alpha(0.0f).setDuration(100)
            animator.withEndAction {
                addRecordButton.visibility = View.INVISIBLE
                homeModel.changingAddButtonState.value = false
            }
            animator.start()
        }
    }

    private fun showAddButton() {
        if (!homeModel.changingAddButtonState.value!!) {
            homeModel.changingAddButtonState.value = true
            addRecordButton.visibility = View.VISIBLE
            val animator = addRecordButton.animate().alpha(1.0f).setDuration(100)
            animator.withEndAction {
                homeModel.changingAddButtonState.value = false
            }
            animator.start()
        }
    }

    private fun showDefaultToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    fun processData(it: String?) {
        clearRecordsView()
        showMore()
    }

    fun clearRecordsView() {
        showedRecords = 0
        mainList.removeAllViews()
    }

    override fun onBackPressed() {
        val toast = Toast.makeText(this, "Press log out button for logging out", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    fun logoutButtonClicked(view: View) {
        User.clearCredentials(this)
        finish()
    }

    fun showMore() {
        val application = applicationContext as App
        if (showedRecords != application.records.size) {
            val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val delta = Math.min(DEFAULT_COUNT_OF_ELEMENTS, application.records.size - showedRecords)
            for (index in showedRecords until(showedRecords + delta)) {
                val recordRow = inflater.inflate(R.layout.record_layout, mainList, false)
                recordRow.recordNameField.text = application.records[index].name
                recordRow.recordValue.text = application.records[index].value.toString()
                recordRow.dateField.text = application.records[index].date.toString()
                recordRow.walletField.text = application.records[index].wallet
                recordRow.setOnClickListener {
                    val intent = Intent(this, FullDescriptionActivity::class.java)
                    intent.putExtra("index", index)
                    val options = ActivityOptions.makeSceneTransitionAnimation(this,
                        Pair<View, String>(recordRow.recordNameField, "recordNameTransition"),
                        Pair<View, String>(recordRow.recordValue, "recordValueTransition"))
                    startActivity(intent, options.toBundle())
                }
                mainList.addView(recordRow)
            }
            showedRecords += delta
            showAddButton()
        }
    }

    fun addButtonClicked(view: View) {
        System.out.println("We're going to start new activity")
        val intent = Intent(this, AddRecordPage::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun tryLoad() {
        val app = applicationContext as App
        homeModel.loading.value = true
        app.loadData(Callback {
            homeModel.loading.value = false
            processData(it)
        }, Callback {
            homeModel.loading.value = false
            showDefaultToast("Error occurred, please try later")
            System.out.println(it)
        })
        app.loadInfo(Callback {
            try {
                if (it!!.has("totalSpend"))
                    homeModel.totalSpend.value = it.getDouble("totalSpend")
                if (it.has("average"))
                    homeModel.averageSpend.value = it.getDouble("average")
                if (it.has("amountOfDays"))
                    homeModel.amountOfDays.value = it.getInt("amountOfDays")
                if (it.has("average30Days"))
                    homeModel.average30Days.value = it.getDouble("average30Days")
                if (it.has("average7Days"))
                    homeModel.average7Days.value = it.getDouble("average7Days")
                if (it.has("daySum")) {
                    val bufferJSON = it.getJSONObject("daySum")
                    val iterator = bufferJSON.keys()
                    val buffer = ArrayList<Double>()
                    iterator.forEach {
                        buffer.add(bufferJSON[it].toString().toDouble())
                    }
                    homeModel.sumDay.value = buffer
                }
            } catch (_: JSONException) {
                showDefaultToast("Error occurred while parsing request")
            }
        }, Callback {
            showDefaultToast(it!!)
        })
    }

    fun reloadButtonClicked(view: View) {
        app.clearRecords()
        homeModel.amountOfDays.value = null
        homeModel.totalSpend.value = null
        homeModel.averageSpend.value = null
        homeModel.average7Days.value = null
        homeModel.average30Days.value = null
        clearRecordsView()
        tryLoad()
    }

    fun loadButtonClick(view: View) {
        val application = applicationContext as App
        if (application.records.size != 0) {
            showMore()
        } else {
            tryLoad()
        }
    }
}
