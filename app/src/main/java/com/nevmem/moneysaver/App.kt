package com.nevmem.moneysaver

import android.app.Application
import android.app.VoiceInteractor
import android.arch.lifecycle.MutableLiveData
import android.util.Log.*
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.Template
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.structure.Callback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*
import kotlin.collections.ArrayList

class App() : Application() {
    var records: ArrayList<Record> = ArrayList()
    lateinit var user: User

    var tags: ArrayList<String> = ArrayList()
    var wallets: ArrayList<String> = ArrayList()
    var templates: ArrayList<Template> = ArrayList()

    var recordsLoading: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var requestQueue: RequestQueue

    init {
        i("APP_CLASS", "App() init method was called")
    }

    fun loadTags() {
        val request = JsonObjectRequest(Request.Method.POST, Vars.ServerApiTags, userCredentialsJSON(), {
            System.out.println(it.toString())
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    val fromJson = it.getJSONArray("data")
                    tags.clear()
                    for (i in 0 until(fromJson.length()))
                        tags.add(fromJson[i].toString())
                } else if(it.getString("type") == "error") {
                    System.out.println(it.getString("error"))
                } else  {
                    System.out.println("Unknown server response format")
                }
            }
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
                wallets.clear()
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

    fun loadAll() {
        loadTags()
        loadWallets()
    }

    override fun onCreate() {
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
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
        date.put("hour", curCalendar.get(Calendar.HOUR_OF_DAY))
        date.put("minute", curCalendar.get(Calendar.MINUTE))
        return date
    }

    private fun createDateFromRecord(record: Record): JSONObject {
        val date = JSONObject()
        date.put("year", record.date.year)
        date.put("month", record.date.month)
        date.put("day", record.date.day)
        date.put("hour", record.date.hour)
        date.put("minute", record.date.minute)
        return date
    }

    private fun processRow(json: JSONObject): Record {
        val record = Record()

        record.name = json.get("name") as String
        record.value = -java.lang.Double.valueOf(json.get("value").toString())
        record.id = json.getString("_id")
        try {
            record.wallet = json.get("wallet") as String
        } catch (_: ClassCastException) {
            record.wallet = "Not set"
        } catch (_: JSONException) {
            record.wallet = "Not set"
        }

        if (json.has("daily")) {
            record.daily = json.getBoolean("daily")
        } else {
            System.out.println("Daily field is not found")
        }

        try {
            val date = json.get("date") as JSONObject
            val year = Integer.valueOf(date.get("year").toString())
            val month = Integer.valueOf(date.get("month").toString())
            val day = Integer.valueOf(date.get("day").toString())
            val hour = Integer.valueOf(date.get("hour").toString())
            val minute = Integer.valueOf(date.get("minute").toString())
            record.date.year = year
            record.date.month = month
            record.date.day = day
            record.date.hour = hour
            record.date.minute = minute
        } catch (_: JSONException) {
            System.out.println("Date is corrupted")
        }

        try {
            val tags = json.get("tags") as JSONArray
            for (i in 0 until tags.length())
                record.tags.add(tags.getString(i))
        } catch (_ : ClassCastException) {
            record.tags.add("Not set")
        }

        return record
    }

    private fun processGetDataResponse(array: JSONArray): java.util.ArrayList<Record> {
        val parsed = java.util.ArrayList<Record>()
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
        var result = java.util.ArrayList<Record>()
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
                clearRecords()
                saveRecords(result)
                System.out.println("Was loaded: ${result.size} records")
            }
        }
    }

    fun loadData(onSuccess: Callback<String>, onError: Callback<String>) {
        recordsLoading.value = true
        val stringRequest = object : StringRequest(Request.Method.POST, Vars.ServerApiData, {
            processData(it)
            onSuccess.callback(it)
            recordsLoading.value = false
        }, {
            recordsLoading.value = false
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

    fun makeAddRequest(name: String, value: Double, tag: String?, wallet: String?,
                       onSuccess: Callback<String>, onError: Callback<String>) {
        val params = userCredentialsJSON()
        params.put("name", name)
        params.put("value", value)
        params.put("wallet", wallet)
        params.put("date", createDate())
        params.put("daily", true) // TODO: hardcoded value
        val tags = JSONArray()
        tags.put(tag)
        params.put("tags", tags)
        System.out.println(params.toString())

        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiAdd, params, {
            System.out.println(it.toString())
            if (it.has("type")) {
                if (it.getString("type") == "error") {
                    onError.callback(it.getString("error"))
                } else if (it.getString("type") == "ok") {
                    onSuccess.callback("Record was successfully added")
                } else {
                    onError.callback("Server response has unknown format")
                }
            } else {
                onError.callback("Sever response has unknown format")
            }
        }, {
            System.out.println(it)
            onError.callback("Internet error")
        })
        requestQueue.add(jsonRequest)
    }

    fun sendEditRequest(record: Record, onSuccess: Callback<String>, onError: Callback<String>) {
        val params = userCredentialsJSON()
        params.put("id", record.id)
        params.put("name", record.name)
        params.put("value", record.value)
        params.put("wallet", record.wallet)
        params.put("daily", record.daily)
        val tags = JSONArray()
        tags.put(record.tags[0])
        params.put("tags", tags)
        params.put("date", createDateFromRecord(record))
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiEdit, params, {
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    onSuccess.callback("Saved")
                } else if (it.getString("type") == "error") {
                    onError.callback(it.getString("error"))
                } else {
                    onError.callback("Server response has unknown format")
                }
            } else {
                onError.callback("Server response has unknown format")
            }
        }, {
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
