package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound
import org.json.JSONArray
import org.json.JSONException

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
        val request = StringRequest(Request.Method.POST, "http://104.236.71.129/api/tags", {
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


    fun loadWallets() {
        val request = StringRequest(Request.Method.POST, "http://104.236.71.129/api/wallets", {
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


    fun saveRecords(from: ArrayList<Record>) {
        for (index in 0 until(from.size))
            records.add(from[index])
        i("APP_CLASS", "Saved ${from.size} records")
    }
}
