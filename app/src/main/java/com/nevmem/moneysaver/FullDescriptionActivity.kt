package com.nevmem.moneysaver

import android.app.Activity
import android.app.AlertDialog
import android.app.SharedElementCallback
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.util.Log.i
import android.view.View
import kotlinx.android.synthetic.main.full_description.*
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.NumberPicker
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.structure.Callback


class FullDescriptionActivity: FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    private var index: Int = 0
    private lateinit var app: App
    private lateinit var viewModel: FullDescriptionActivityViewModel

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        viewModel = ViewModelProviders.of(this).get(FullDescriptionActivityViewModel::class.java)

        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)

        i("description", "Hello from on create method")
        app = applicationContext as App
        index = intent.extras["index"].toString().toInt()

        recordNameField.text = app.records[index].name
        recordValueField.text = app.records[index].value.toString()

        window.sharedElementEnterTransition.duration = 200

        viewModel.currentYear.observe(this, Observer {
            year.text = it
            if (viewModel.prevYear.value != it) {
                year.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                year.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
            comparePrevCurrent()
        })
        viewModel.currentMonth.observe(this, Observer {
            month.text = it
            if (viewModel.prevMonth.value != it) {
                month.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                month.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
            comparePrevCurrent()
        })
        viewModel.currentDay.observe(this, Observer {
            day.text = it
            if (viewModel.prevDay.value != it) {
                day.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                day.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
            comparePrevCurrent()
        })
        viewModel.currentHour.observe(this, Observer {
            hour.text = it
            if (viewModel.prevHour.value != it) {
                hour.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                hour.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
            comparePrevCurrent()
        })
        viewModel.currentMinute.observe(this, Observer {
            minute.text = it
            if (viewModel.prevMinute.value != it) {
                minute.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                minute.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
            comparePrevCurrent()
        })

        viewModel.currentDaily.observe(this, Observer {
            if (it != null) {
                dailySwitch.isChecked = it!! == true
            } else {
                dailySwitch.isChecked = false
            }
            comparePrevCurrent()
        })

        viewModel.needChange.observe(this, Observer {
            if (it!!) {
                saveChangesButton.visibility = View.VISIBLE
            } else {
                saveChangesButton.visibility = View.GONE
            }
        })

        viewModel.error.observe(this, Observer {
            if (!it!!.isEmpty()) {
                msg.text = it
                msg.setTextColor(ContextCompat.getColor(this, R.color.dangerColor))
            } else {
                msg.text = ""
            }
        })

        viewModel.success.observe(this, Observer {
            if (!it!!.isEmpty()) {
                msg.text = it
                msg.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                msg.text = ""
            }
        })

        yearWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.maxValue = 2020
            picker.minValue = 2018
            picker.value = viewModel.currentYear.value!!.toInt()
            builder.setTitle("Choose year")
                .setMessage("Please enter new year for this record")
                .setPositiveButton(android.R.string.yes) { _, which -> run {
                        viewModel.currentYear.value = picker.value.toString()
                    } }
                .setNegativeButton(android.R.string.no) { _, which -> run {
                    System.out.println(which)
                } }
                .setView(picker)
                .show()
        }

        monthWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 0
            picker.maxValue = 11
            picker.displayedValues = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
            for (index in 0 until(picker.displayedValues.size))
                if (picker.displayedValues[index] == viewModel.currentMonth.value)
                    picker.value = index
            builder.setTitle("Choose month")
                .setMessage("Please choose new month for this record")
                .setPositiveButton(android.R.string.yes) { _, which -> run {
                    val newMonth = picker.displayedValues[picker.value]
                    viewModel.currentMonth.value = newMonth
                }}
                .setView(picker)
                .show()
        }

        dayWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 1
            picker.maxValue = amountOfDaysInMonth(viewModel.currentYear.value!!.toInt(), viewModel.currentMonth.value.toString())
            picker.value = viewModel.currentDay.value!!.toInt()
            builder.setTitle("Choose day of month")
                .setMessage("Please choose new day for this record")
                .setPositiveButton(android.R.string.yes) { _, which -> run {
                    viewModel.currentDay.value = fillToFormat(picker.value.toString())
                }}
                .setView(picker)
                .show()
        }

        hourWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 0
            picker.maxValue = 23
            picker.value = viewModel.currentHour.value!!.toInt()
            builder.setTitle("Choose hour of a day")
                .setMessage("Please choose new hour for this record")
                .setPositiveButton(android.R.string.yes) { _, which -> run {
                    viewModel.currentHour.value = fillToFormat(picker.value.toString())
                }}
                .setView(picker)
                .show()
        }

        minuteWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 0
            picker.maxValue = 59
            picker.value = viewModel.currentMinute.value!!.toInt()
            builder.setTitle("Choose minute of a hour")
                .setMessage("Please choose new minute for this record")
                .setPositiveButton(android.R.string.yes) { _, which -> run {
                    viewModel.currentMinute.value = fillToFormat(picker.value.toString())
                }}
                .setView(picker)
                .show()
        }

        dailySwitch.setOnCheckedChangeListener { _, isChecked -> run {
            viewModel.currentDaily.value = isChecked
        } }

        saveChangesButton.setOnClickListener {
            val record = Record()
            record.tags.add(tag.text.toString())
            record.wallet = wallet.text.toString()
            record.id = app.records[index].id
            record.value = recordValueField.text.toString().toDouble()
            record.name = recordNameField.text.toString()
            record.daily = dailySwitch.isChecked

            record.date.year = viewModel.currentYear.value!!.toInt()
            record.date.month = getIntByMonth(viewModel.currentMonth.value!!)
            record.date.day = viewModel.currentDay.value!!.toInt()
            record.date.hour = viewModel.currentHour.value!!.toString().toInt()
            record.date.minute = viewModel.currentMinute.value!!.toString().toInt()

            viewModel.success.value = ""
            viewModel.error.value = ""

            app.sendEditRequest(record, Callback {
                viewModel.success.value = it
            }, Callback {
                viewModel.error.value = it
            })
        }

        fillValues()

        Handler().postDelayed({
            fadeInValues()
        }, 400)
    }

    private fun fillToFormat(current: String): String {
        var res = current
        while (res.length < 2)
            res = "0$res"
        return current
    }

    private fun comparePrevCurrent() {
        var diffCount = 0
        if (viewModel.currentYear.value != viewModel.prevYear.value) diffCount++
        if (viewModel.currentMonth.value != viewModel.prevMonth.value) diffCount++
        if (viewModel.currentDay.value != viewModel.prevDay.value) diffCount++
        if (viewModel.currentHour.value != viewModel.prevHour.value) diffCount++
        if (viewModel.currentMinute.value != viewModel.prevMinute.value) diffCount++
        if (viewModel.currentDaily.value != viewModel.prevDaily.value) diffCount++
        viewModel.needChange.value = (diffCount != 0)
    }

    private fun isLeap(year: Int): Boolean {
        return (year % 4) == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    private fun getIntByMonth(month: String): Int {
        when (month) {
            "January" -> return 1
            "February" -> return 2
            "March" -> return 3
            "April" -> return 4
            "May" -> return 5
            "June" -> return 6
            "July" -> return 7
            "August" -> return 8
            "September" -> return 9
            "October" -> return 10
            "November" -> return 11
            "December" -> return 12
        }
        return 13
    }

    private fun amountOfDaysInMonth(year: Int, month: String): Int {
        var leap = 0
        if (isLeap(year))
            leap = 1
        when (month) {
            "January", "March", "May", "July", "August", "October", "December" -> return 31
            "February" -> return 28 + leap
        }
        return 30
    }

    private fun getMonth(index: Int): String {
        when(index - 1) {
            0->return "January"
            1->return "February"
            2->return "March"
            3->return "April"
            4->return "May"
            5->return "June"
            6->return "July"
            7->return "August"
            8->return "September"
            9->return "October"
            10->return "November"
            11->return "December"
        }
        return "Unknown"
    }

    fun fillValues() {
        tag.text = app.records[index].tags[0]
        wallet.text = app.records[index].wallet

        viewModel.currentYear.value = app.records[index].date.year.toString()
        viewModel.prevYear.value = app.records[index].date.year.toString()

        viewModel.currentMonth.value = getMonth(app.records[index].date.month)
        viewModel.prevMonth.value = getMonth(app.records[index].date.month)

        viewModel.currentDay.value = fillToFormat(app.records[index].date.day.toString())
        viewModel.prevDay.value = fillToFormat(app.records[index].date.day.toString())

        viewModel.currentHour.value = fillToFormat(app.records[index].date.hour.toString())
        viewModel.prevHour.value = fillToFormat(app.records[index].date.hour.toString())

        viewModel.currentMinute.value = fillToFormat(app.records[index].date.minute.toString())
        viewModel.prevMinute.value = fillToFormat(app.records[index].date.minute.toString())

        viewModel.currentDaily.value = app.records[index].daily
        viewModel.prevDaily.value = app.records[index].daily
    }

    fun fadeInValues() {
        tag.animate().alpha(1f).setDuration(200).start()
        wallet.animate().alpha(1f).setDuration(200).start()
        yearWrapper.animate().alpha(1f).setDuration(200).start()
        monthWrapper.animate().alpha(1f).setDuration(200).start()
        dayWrapper.animate().alpha(1f).setDuration(200).start()
        hourWrapper.animate().alpha(1f).setDuration(200).start()
        minuteWrapper.animate().alpha(1f).setDuration(200).start()
        info_text_1.animate().alpha(1f).setDuration(200).start()
        dailySwitch.animate().alpha(1f).setDuration(200).start()
    }
}