package com.nevmem.moneysaver

import android.app.Application
import android.os.Handler
import android.util.Log.*
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.nevmem.moneysaver.data.*
import com.nevmem.moneysaver.structure.Callback
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*
import kotlin.collections.ArrayList

class App() : Application() {
    lateinit var user: User
    var info: Info

    var tags: ArrayList<String> = ArrayList()
    var wallets: ArrayList<String> = ArrayList()
    var records: ArrayList<Record> = ArrayList()

    var templates: Templates

    var infoFlow: BehaviorSubject<Info>
    var recordsFlow: BehaviorSubject<ArrayList<Record>>
    var templatesFlow: BehaviorSubject<Templates>

    var changeFlow: BehaviorSubject<RecordChangeableWrapper>
    lateinit var requestQueue: RequestQueue

    init {
        i("APP_CLASS", "App() init method was called")
        info = Info()
        templates = Templates()
        infoFlow = BehaviorSubject.create()
        recordsFlow = BehaviorSubject.create()
        templatesFlow = BehaviorSubject.create()
        changeFlow = BehaviorSubject.create()
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

    fun loadTemplates() {
        templates.clear()
        templates.ready = false
        templatesFlow.onNext(templates)
        val request = object : StringRequest(Request.Method.POST, Vars.ServerApiTemplates, {
            try {
                val response = JSONObject(it)
                if (response.has("type")) {
                    if (response.getString("type") == "ok") {
                        val arr = response.getJSONArray("data")
                        for (index in 0 until (arr.length())) {
                            val jsonObj = arr.getJSONObject(index)
                            println(jsonObj.toString())
                            val name = jsonObj.getString("name")
                            val value = jsonObj.getDouble("value")
                            val wallet = jsonObj.getString("wallet")
                            val tag = jsonObj.getString("tag")
                            val id = jsonObj.getString("id")
                            var template = Template(name, value, tag, wallet, id)
                            templates.add(template)
                        }
                        templates.ready = true
                        templatesFlow.onNext(templates)
                    } else if (response.getString("type") == "error") {
                        println(response.getString("error"))
                    } else {
                        println("Server response has unknown format")
                    }
                } else {
                    println("Server response has unknown format")
                }
            } catch (_: JSONException) {
                println("JSON parse exception")
            }
        }, {

        }) {
            override fun getBody(): ByteArray {
                return userCredentialsJSON().toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        requestQueue.add(request)
    }

    fun userCredentialsJSON(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
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
        recordsFlow.onNext(records)
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
        info = Info()
        infoFlow.onNext(info)
        System.out.println("Hello from info loader")
        val options = userCredentialsJSON()
        options.put("daysDescription", "true")
        options.put("info7", "true")
        options.put("info30", "true")
        options.put("months", "true")
        val jsonRequest = JsonObjectRequest(Request.Method.POST, Vars.ServerApiInfo, options,
            {
                if (!it.has("type")) {
                    onError.callback("Server response has unknown format")
                } else {
                    if (it.getString("type") == "ok") {
                        info.fromJSON(it.getJSONObject("info"))
                        infoFlow.onNext(info)
                        onSuccess.callback(it.getJSONObject("info"))
                    } else {
                        onError.callback("Server error")
                    }
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
            clearRecords()
            saveRecords(result)
            System.out.println("Was loaded: ${result.size} records")
        }
    }

    fun loadData() {
        val stringRequest = object : StringRequest(Request.Method.POST, Vars.ServerApiData, {
            processData(it)
        }, {
            println(it.toString())
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

    fun checkData() {
        if (!info.ready)
            loadInfo(Callback {}, Callback {})
        if (tags.size == 0)
            loadTags()
        if (wallets.size == 0)
            loadWallets()
        loadData()
    }

    fun saveRecords(from: ArrayList<Record>) {
        for (index in 0 until(from.size))
            records.add(from[index])
        recordsFlow.onNext(records)
        i("APP_CLASS", "Saved ${from.size} records")
    }

    fun createNewTemplate(base: TemplateBase) {
        val params = userCredentialsJSON()
        params.put("name", base.name)
        params.put("value", base.value)
        params.put("wallet", base.wallet)
        params.put("tag", base.tag)
        val request = JsonObjectRequest(Request.Method.POST, Vars.ServerApiCreateTemplate, params, {
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    Toast.makeText(this, "Template was successfully added", Toast.LENGTH_LONG).show()
                } else if (it.getString("type") == "error") {
                    Toast.makeText(this, it.getString("error"), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Server response has unknown format", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Server response has unknown format", Toast.LENGTH_LONG).show()
            }
            loadTemplates()
        }, {
            Toast.makeText(this, "Internet error", Toast.LENGTH_LONG).show()
        })
        requestQueue.add(request)
    }

    fun removeTemplate(id: String) {
        val params = userCredentialsJSON()
        params.put("templateId", id)
        val request = JsonObjectRequest(Request.Method.POST, Vars.ServerApiRemoveTemplate, params, {
            println(it)
            loadTemplates()
        }, {
            println(it)
            loadTemplates()
        })
        requestQueue.add(request)
    }

    fun emitChangeRecordError(error: String) {
        var bufferChangeable = changeFlow.value
        if (bufferChangeable != null) {
            bufferChangeable.error = error
            bufferChangeable.success = ""
            bufferChangeable.loading = false
            changeFlow.onNext(bufferChangeable)
        }
    }

    fun emitChangeRecordSuccess(success: String) {
        var bufferChangeable = changeFlow.value
        if (bufferChangeable != null) {
            bufferChangeable.success = success
            bufferChangeable.error = ""
            bufferChangeable.loading = false
            changeFlow.onNext(bufferChangeable)
        }
    }

    fun editFromDescription(record: Record) {
        var bufferChangeable = changeFlow.value
        if (bufferChangeable != null) {
            bufferChangeable.loading = true
            changeFlow.onNext(bufferChangeable)
            val params = userCredentialsJSON()
            params.put("name", record.name)
            params.put("value", record.value)
            params.put("wallet", record.wallet)
            params.put("tags", record.tagsToJSON())
            params.put("date", record.date.toJSON())
            params.put("id", record.id)
            println(record.date.toJSON().toString())
            params.put("daily", record.daily)
            val request = JsonObjectRequest(Request.Method.POST, Vars.ServerApiEdit, params, {
                println(it.toString())
                if (it.has("type")) {
                    if (it.getString("type") == "ok") {
                        val buffer = changeFlow.value
                        if (buffer != null) {
                            buffer.record = record
                            changeFlow.onNext(buffer)
                        }
                        emitChangeRecordSuccess("Success")
                    } else if (it.getString("type") == "error") {
                        emitChangeRecordError(it.getString("error"))
                    } else {
                        emitChangeRecordError("Server response has unknown format")
                    }
                } else {
                    emitChangeRecordError("Server response has unknown format")
                }
            }, {
                print(it.toString())
            })
            requestQueue.add(request)
        }
    }

    fun deleteRecord(record: Record) {
        val params = userCredentialsJSON()
        params.put("record_id", record.id)
        val request = object : StringRequest(Request.Method.POST, Vars.ServerApiDeleteRecord, {
            loadData()
        }, {
            println(it.toString())
        }) {
            override fun getBody(): ByteArray {
                return params.toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        requestQueue.add(request)
    }
}
