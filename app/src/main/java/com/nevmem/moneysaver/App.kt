package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.i
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.dagger.components.AppComponent
import com.nevmem.moneysaver.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.data.*
import com.nevmem.moneysaver.structure.Callback
import io.reactivex.subjects.BehaviorSubject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class App() : Application() {
    lateinit var user: User
    var info: Info

    var tags: ArrayList<String> = ArrayList()
    var wallets: ArrayList<String> = ArrayList()

    var templates: Templates

    var infoFlow: BehaviorSubject<Info>
    var templatesFlow: BehaviorSubject<Templates>

    var changeFlow: BehaviorSubject<RecordChangeableWrapper>
    lateinit var requestQueue: RequestQueue

    var appComponent: AppComponent

    init {
        i("APP_CLASS", "App() init method was called")
        info = Info()
        templates = Templates()
        infoFlow = BehaviorSubject.create()
        templatesFlow = BehaviorSubject.create()
        changeFlow = BehaviorSubject.create()

        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule(this))
            .networkModule(NetworkModule(this))
            .build()
    }

    private fun loadTags() {
        val request = JsonObjectRequest(Request.Method.POST, Vars.ServerApiTags, userCredentialsJSON(), {
            System.out.println(it.toString())
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    val fromJson = it.getJSONArray("data")
                    tags.clear()
                    for (i in 0 until (fromJson.length()))
                        tags.add(fromJson[i].toString())
                } else if (it.getString("type") == "error") {
                    System.out.println(it.getString("error"))
                } else {
                    System.out.println("Unknown server response format")
                }
            }
        }, {
            System.out.println(it.toString())
        })
        requestQueue.add(request)
    }

    private fun userCredentialsJSON(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }

    fun loadAll() {
        loadTags()
    }

    override fun onCreate() {
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
        i("APP_CLASS", "onCreate method was called")
    }

    fun isValidUserInfo(json: JSONObject): Boolean {
        if (json.has("token") && json.has("first_name") &&
            json.has("last_name") && json.has("login")
        )
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
                        val parsedUser = User(
                            it.getString("login"), it.getString("token"),
                            it.getString("first_name"), it.getString("last_name")
                        )

                        User.saveUserCredentials(this, parsedUser)
                        user = parsedUser
                        onSuccess.callback(null)
                    }
                }
            }, {
                onError.callback(it.toString())
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
            }, {
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

    fun makeAddRequest(
        name: String, value: Double, tag: String?, wallet: String?,
        onSuccess: Callback<String>, onError: Callback<String>
    ) {
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

    fun checkData() {
        if (!info.ready)
            loadInfo(Callback {}, Callback {})
        if (tags.size == 0)
            loadTags()
    }
}
