package com.nevmem.moneysaver

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log.i
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import kotlinx.android.synthetic.main.full_description.*
import javax.inject.Inject


class FullDescriptionActivity : FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    private var index: Int = 0
    private lateinit var app: App
    private lateinit var viewModel: FullDescriptionActivityViewModel

    @Inject
    lateinit var networkQueue: NetworkQueue
    @Inject
    lateinit var historyRepo: HistoryRepository
    private lateinit var record: Record

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        viewModel = ViewModelProviders.of(this).get(FullDescriptionActivityViewModel::class.java)

        window.statusBarColor = Color.parseColor("#101010")

        i("FDA", "Hello from on create method")
        app = applicationContext as App
        val intentBundle = intent.extras
        if (intentBundle != null && intentBundle.containsKey("index")) {
            index = intentBundle.toString().toInt()
        }

        window.sharedElementEnterTransition.duration = 200

        /* Dagger2 injection */
        app.appComponent.inject(this)

        record = historyRepo.getRecordOnIndex(index)

        fillValues()

        Handler().postDelayed({
            fadeInValues()
        }, 400)
    }

    private fun fillValues() {
        year.text = record.date.year.toString()
        month.text = record.date.month.toString()
        day.text = record.date.day.toString()
        hour.text = record.date.hour.toString()
        minute.text = record.date.minute.toString()
        wallet.text = record.wallet
        tag.text = record.tag
        recordValueField.text = record.value.toString()
        recordNameField.text = record.name
    }

    private fun setupLiveDateObservers() {
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

    }

    private fun setupListeners() {
        yearWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.maxValue = 2020
            picker.minValue = 2018
            val changeable = viewModel.record.value
            if (changeable != null) {
                picker.value = changeable.date.year
                builder.setTitle("Choose year")
                    .setMessage("Please enter new year for this record")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        run {
                            changeable.date.year = picker.value
                            viewModel.record.postValue(changeable)
                        }
                    }
                    .setNegativeButton(android.R.string.no) { _, which ->
                        run {
                            System.out.println(which)
                        }
                    }
                    .setView(picker)
                    .show()
            }
        }

        /* monthWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 0
            picker.maxValue = 11
            picker.displayedValues = arrayOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            )
            for (index in 0 until (picker.displayedValues.size))
                if (picker.displayedValues[index] == viewModel.currentMonth.value)
                    picker.value = index
            builder.setTitle("Choose month")
                .setMessage("Please choose new month for this record")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    run {
                        val newMonth = picker.displayedValues[picker.value]
                        viewModel.currentMonth.value = newMonth
                    }
                }
                .setView(picker)
                .show()
        }

        dayWrapper.setOnClickListener {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            val picker = NumberPicker(this)
            picker.minValue = 1
            picker.maxValue =
                amountOfDaysInMonth(viewModel.currentYear.value!!.toInt(), viewModel.currentMonth.value.toString())
            picker.value = viewModel.currentDay.value!!.toInt()
            builder.setTitle("Choose day of month")
                .setMessage("Please choose new day for this record")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    run {
                        viewModel.currentDay.value = fillToFormat(picker.value.toString())
                    }
                }
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
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    run {
                        viewModel.currentHour.value = fillToFormat(picker.value.toString())
                    }
                }
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
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    run {
                        viewModel.currentMinute.value = fillToFormat(picker.value.toString())
                    }
                }
                .setView(picker)
                .show()
        } */

        dailySwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                val changeable = viewModel.record.value
                if (changeable != null) {
                    changeable.daily = isChecked
                    viewModel.record.postValue(changeable)
                }
            }
        }

        saveChangesButton.setOnClickListener {
            val record = viewModel.record.value
            if (record != null) {
                viewModel.success.value = ""
                viewModel.error.value = ""
                // TODO: (real editions)
            } else {
                Toast.makeText(this, "Record value is null", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fillToFormat(current: String): String {
        var res = current
        while (res.length < 2)
            res = "0$res"
        return current
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
        when (index - 1) {
            0 -> return "January"
            1 -> return "February"
            2 -> return "March"
            3 -> return "April"
            4 -> return "May"
            5 -> return "June"
            6 -> return "July"
            7 -> return "August"
            8 -> return "September"
            9 -> return "October"
            10 -> return "November"
            11 -> return "December"
        }
        return "Unknown"
    }

    private fun fadeInValues() {
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