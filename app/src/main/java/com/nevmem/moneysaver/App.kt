package com.nevmem.moneysaver

import android.app.Application
import android.app.VoiceInteractor
import android.util.Log.*
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.structure.Callback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class App() : Application() {
    var records: ArrayList<Record> = ArrayList()
    lateinit var user: User

    var tags: ArrayList<String> = ArrayList()
    var wallets: ArrayList<String> = ArrayList()

    lateinit var requestQueue: RequestQueue

    init {
        i("APP_CLASS", "App() init method was called")
    }

    fun loadTags() {
        val request = StringRequest(Request.Method.POST, Vars.ServerApiTags, {
            try {
                val json = JSONArray(it)
                for (i in 0 until(json.length())) {
                    tags.add(json[i].toString())
                }
            } catch (_: JSONException) { }
        }, {
            System.out.println(it.toString())
        })
        requestQueue.add(request)
    }

    fun userCredentialsJSON(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }


    fun loadWallets() {
        val request = StringRequest(Request.Method.POST, Vars.ServerApiWallets, {
            try {
                val json = JSONArray(it)
                for (i in 0 until (json.length())) {
                    wallets.add(json[i].toString())
                }
            } catch (_: JSONException) { }
        }, {
            System.out.println(it.toString())
        })
        requestQueue.add(request)
    }

    override fun onCreate() {
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
        loadTags()
        loadWallets()
        i("APP_CLASS", "onCreate method was called")
    }

    fun clearRecords() {
        records.clear()
        i("APP_CLASS", "records array was cleared")
    }

    fun isValidUserInfo(json: JSONObject): Boolean {
        if (json.has("token") && json.has("first_name") &&
            json.has("last_name") && json.has("login"))
            return true
        return false
    }

    fun tryLogin(login: String, password: String, onSuccess: Callback<String>, onError: Callback<String>) {
        val params = JSONObject()
        params.put("login", login)
        params.put("password", password)
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiLogin,
            params,
            {
                System.out.println(it.toString())
                if (it.has("err")) {
                    onError.callback(it.getString("err"))
                } else {
                    if (!isValidUserInfo(it)) {
                        onError.callback("Wrong server response")
                    } else {
                        val parsedUser = User(it.getString("login"), it.getString("token"),
                            it.getString("first_name"), it.getString("last_name"))

                        User.saveUserCredentials(this, parsedUser)
                        user = parsedUser
                        onSuccess.callback(null)
                    }
                }
            }, {
                System.out.println(it.toString())
                onError.callback("Internet error")
            })
        requestQueue.add(jsonRequest)
    }

    fun loadInfo(onSuccess: Callback<JSONObject>, onError: Callback<String>) {
        val options = userCredentialsJSON()
        options.put("daysDescription", "true")
        options.put("info7", "true")
        options.put("info30", "true")
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiInfo, options,
            {
                if (!it.has("type")) {
                    onError.callback("Server response has unknown format")
                } else {
                    if (it.getString("type") == "ok")
                        onSuccess.callback(it.getJSONObject("info"))
                    else
                        onError.callback("Server error")
                }
            },{
                System.out.println(it.toString())
                onError.callback("Internet error")
            })
        requestQueue.add(jsonRequest)
    }

    private fun createDate(): JSONObject {
        val curCalendar = Calendar.getInstance()
        val date = JSONObject()
        date.put("year", curCalendar.get(Calendar.YEAR))
        date.put("month", curCalendar.get(Calendar.MONTH) + 1)
        date.put("day", curCalendar.get(Calendar.DATE))
        date.put("hour", curCalendar.get(Calendar.HOUR))
        date.put("minute", curCalendar.get(Calendar.MINUTE))
        return date
    }

    fun loadData(onSuccess: Callback<String>, onError: Callback<String>) {
        val stringRequest = object : StringRequest(Request.Method.POST, Vars.ServerApiData, {
            onSuccess.callback(it)
        }, {
            onError.callback(it.toString())
        }) {
            override fun getBody(): ByteArray {
                return userCredentialsJSON().toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        requestQueue.add(stringRequest)
    }

    fun makeAddRequest(name: String, value: String, tag: String?, wallet: String?,
                       onSuccess: Callback<String>, onError: Callback<String>) {
        val params = userCredentialsJSON()
        params.put("name", name)
        params.put("value", value)
        params.put("wallet", wallet)
        params.put("date", createDate())
        val tags = JSONArray()
        tags.put(tag)
        params.put("tags", tags)

        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiAdd, params, {
            if (it.has("data")) {
                onSuccess.callback(it.getString("data"))
            } else if (it.has("err")) {
                onError.callback(it.getString("err"))
            } else {
                onError.callback("Server response has unknown format")
            }
        }, {
            System.out.println(it)
            onError.callback("Internet error")
        })
        requestQueue.add(jsonRequest)
    }

    fun saveRecords(from: ArrayList<Record>) {
        for (index in 0 until(from.size))
            records.add(from[index])
        i("APP_CLASS", "Saved ${from.size} records")
    }
}
