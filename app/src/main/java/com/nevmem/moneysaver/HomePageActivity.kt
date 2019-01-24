package com.nevmem.moneysaver

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_page.*
import kotlinx.android.synthetic.main.user_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomePageActivity : FragmentActivity() {

    lateinit var homeModel: HomePageActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            averageDay.text = it!!.toString()
        })
        homeModel.totalSpend.observe(this, Observer<Double> {
            totalSpend.text = it!!.toString()
        })
        homeModel.loading.observe(this, Observer {
            if (it == true)
                loadingBar.visibility = View.VISIBLE
            else
                loadingBar.visibility = View.GONE
        })

//        homePage.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY -> run{ // TODO:for endless scrolling
//            if (topLinear.height - 100 <= scrollY + homePage.height) {
//                System.out.println("Update needed")
//            }
//        } }
    }

    fun processRow(json: JSONObject): Record {
        val record = Record()

        record.name = json.get("name") as String
        record.value = -java.lang.Double.valueOf(json.get("value").toString())
        record.wallet = json.get("wallet") as String

        val date = json.get("date") as JSONObject
        val year = Integer.valueOf(date.get("year").toString())
        val month = Integer.valueOf(date.get("month").toString())
        val day = Integer.valueOf(date.get("day").toString())
        val hour = Integer.valueOf(date.get("hour").toString())
        val minute = Integer.valueOf(date.get("minute").toString())

        val tags = json.get("tags") as JSONArray
        for (i in 0 until tags.length())
        record.tags.add(tags.getString(i))

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
                val toast = Toast.makeText(this, "Loaded records", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 40)
                toast.show()

                var sum = 0.0
                var amountOfDays = 50

                for (i in 0 until(result.size)) {
                    sum += result[i].value
                }

                homeModel.totalSpend.value = sum
                homeModel.amountOfDays.value = amountOfDays

                homeModel.averageSpend.value = sum / amountOfDays

                val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                mainList.removeAllViews()

                for (index in 0 until(Math.min(15, result.size))) {
                    val recordView = inflater.inflate(R.layout.record_layout, mainList, false)
                    val nameField: TextView = recordView.findViewById(R.id.recordName)
                    nameField.text = result[index].name
                    val valueField: TextView = recordView.findViewById(R.id.recordValue)
                    valueField.text = java.lang.Double.valueOf(result[index].value).toString()
                    val dateField: TextView = recordView.findViewById(R.id.dateField)
                    dateField.text = result[index].date.toString()
                    recordView.setOnClickListener {
                        System.out.println("Hello from this kek " + result[index].name)
                    }
                    mainList.addView(recordView)
                }
            }
        }
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

            val nameField: TextView = recordView.findViewById(R.id.recordName)
            nameField.text = values[index].name

            val valueField: TextView = recordView.findViewById(R.id.recordValue)
            valueField.text = java.lang.Double.valueOf(values[index].value).toString()

            val dateField: TextView = recordView.findViewById(R.id.dateField)
            dateField.text = values[index].date.toString()

            return recordView
        }
    }

    fun loadButtonClick(view: View) {
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
                val params = HashMap < String, String >()
                params.put("login", homeModel.user.value!!.login)
                params.put("token", homeModel.user.value!!.token)
                return JSONObject(params).toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        requestQueue.add(stringRequest)
    }
}
