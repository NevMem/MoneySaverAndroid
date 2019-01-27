package com.nevmem.moneysaver

import android.app.ActivityOptions
import android.app.Application
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.record_layout.view.*
import kotlinx.android.synthetic.main.user_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException

class HomePageActivity(var showedRecords: Int = 0) : FragmentActivity() {

    lateinit var homeModel: HomePageActivityViewModel
    val DEFAULT_COUNT_OF_ELEMENTS = 20
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        app = applicationContext as App

        homeModel = ViewModelProviders.of(this).get(HomePageActivityViewModel::class.java)

        try {
            homeModel.user.value = User.loadUserCredintials(this)
        } catch (e: UserCredentialsNotFound) {
            System.out.println("User credentials not found")
        }

        homeModel.user.observe(this, Observer<User> {
            userName.text = it!!.first_name
        })
        homeModel.averageSpend.observe(this, Observer<Double> {
            try {
                averageSpend.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                averageSpend.text = ""
            }
        })
        homeModel.totalSpend.observe(this, Observer<Double> {
            try {
                totalSpend.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                totalSpend.text = ""
            }
        })
        homeModel.amountOfDays.observe(this, Observer<Int> {
            try {
                trackedDays.text = it!!.toString()
            } catch (_: KotlinNullPointerException) {
                trackedDays.text = ""
            }
        })
        homeModel.loading.observe(this, Observer {
            if (it == true)
                loadingBar.visibility = View.VISIBLE
            else
                loadingBar.visibility = View.GONE
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

    fun processRow(json: JSONObject): Record {
        val record = Record()

        record.name = json.get("name") as String
        record.value = -java.lang.Double.valueOf(json.get("value").toString())
        try {
            record.wallet = json.get("wallet") as String
        } catch (_: ClassCastException) {
            record.wallet = "Not set"
        } catch (_: JSONException) {
            record.wallet = "Not set"
        }

        val date = json.get("date") as JSONObject
        val year = Integer.valueOf(date.get("year").toString())
        val month = Integer.valueOf(date.get("month").toString())
        val day = Integer.valueOf(date.get("day").toString())
        val hour = Integer.valueOf(date.get("hour").toString())
        val minute = Integer.valueOf(date.get("minute").toString())

        try {
            val tags = json.get("tags") as JSONArray
            for (i in 0 until tags.length())
                record.tags.add(tags.getString(i))
        } catch (_ : ClassCastException) {
            record.tags.add("Not set")
        }

        record.date.year = year
        record.date.month = month
        record.date.day = day
        record.date.hour = hour
        record.date.minute = minute

        return record
    }

    fun processGetDataResponse(array: JSONArray): ArrayList<Record> {
        val parsed = ArrayList<Record>()
        try {
            for (i in 0 .. array.length()) {
                val now = array.getJSONObject(i)
                val current = processRow(now)
                parsed.add(current)
            }
        } catch (e: JSONException) {
            System.out.println("JSON parse exception")
        }
        return parsed
    }

    private fun showDefaultToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    fun processData(it: String) {
        var parsed = false
        var result = ArrayList<Record>()
        try {
            result = processGetDataResponse(JSONArray(it))
            parsed = true
        } catch (e: JSONException) {
            System.out.println("JSON exception while parsing, server response")
        }
        if (!parsed) {
            try {
                val obj = JSONObject(it)
                val serverError = obj.getString("err")
                if (serverError != null) {
                }
                parsed = true
            } catch (e: JSONException) {
                System.out.println("Another json exception while parsing")
            }
        }

        if (!parsed) {
        } else {
            if (result.size != 0) {
                showDefaultToast("Loaded records")

                val application = applicationContext as App
                application.clearRecords()
                application.saveRecords(result)

                clearRecordsView()

                showMore()
            }
        }
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

    internal inner class MainArrayAdapter(val context: Context, list: List<Record>) : BaseAdapter() {
        var mapper: java.util.HashMap<Record, Long>
        var values: java.util.ArrayList<Record>

        init {
            mapper = java.util.HashMap()
            values = java.util.ArrayList()
            for (i in list.indices) {
                mapper[list[i]] = i.toLong()
                values.add(list[i])
            }
        }

        override fun getCount(): Int {
            return mapper.size
        }

        override fun getItem(position: Int): Record? {
            return values[position]
        }

        override fun getItemId(position: Int): Long {
            val item = getItem(position)
            val current = mapper[item]
            return current!!.toLong()
        }

        override fun getView(index: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val recordView = inflater.inflate(R.layout.record_layout, parent, false)

            val nameField: TextView = recordView.findViewById(R.id.recordNameField)
            nameField.text = values[index].name

            val valueField: TextView = recordView.findViewById(R.id.recordValue)
            valueField.text = java.lang.Double.valueOf(values[index].value).toString()

            val dateField: TextView = recordView.findViewById(R.id.dateField)
            dateField.text = values[index].date.toString()

            return recordView
        }
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
                recordRow.walletField.text = application.records[index].wallet.toString()
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
        val requestQueue = Volley.newRequestQueue(this)
        homeModel.loading.value = true
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            "http://104.236.71.129/api/data",
            {
                homeModel.loading.value = false
                processData(it)
            },
            {
                homeModel.loading.value = false
                System.out.println("Error occurred")
                System.out.println(it.toString())
            }
        ) {
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params.put("login", homeModel.user.value!!.login)
                params.put("token", homeModel.user.value!!.token)
                return JSONObject(params).toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val jsonRequest = JsonObjectRequest(Request.Method.POST, "http://104.236.71.129/api/info", app.userCredentialsJSON(),
            {
                if (!it.has("type")) {
                    showDefaultToast("Server response has unknown format")
                } else {
                    if (it.getString("type").equals("error")) {
                        showDefaultToast("Server error")
                    } else {
                        try {
                            val info = it.getJSONObject("info")
                            System.out.println(info.toString())
                            if (info.has("totalSpend"))
                                homeModel.totalSpend.value = info.getDouble("totalSpend")
                            if (info.has("average"))
                                homeModel.averageSpend.value = info.getDouble("average")
                            if (info.has("amountOfDays"))
                                homeModel.amountOfDays.value = info.getInt("amountOfDays")
                        } catch (_: JSONException) {
                            showDefaultToast("Server response is unable to parse")
                        }
                    }
                }
            }, {
                System.out.println(it.toString())
            })
        requestQueue.add(stringRequest)
        requestQueue.add(jsonRequest)
    }

    fun reloadButtonClicked(view: View) {
        app.clearRecords()
        homeModel.amountOfDays.value = null
        homeModel.totalSpend.value = null
        homeModel.averageSpend.value = null
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
