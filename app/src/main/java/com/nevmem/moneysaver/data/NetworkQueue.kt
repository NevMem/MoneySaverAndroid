package com.nevmem.moneysaver.data

import android.content.Context
import android.os.Handler
import android.util.Log.i
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NetworkQueue(ctx: Context) {
    private var requestQueue: RequestQueue = Volley.newRequestQueue(ctx)
    private val tag = "NET_QUEUE"

    companion object {
        const val DEFAULT_TIMEOUT: Long = 5000
    }

    init {
        i(tag, "NetworkQueue constructor was called")
    }

    fun infinitePostJsonObjectRequest(
        url: String, params: JSONObject,
        resolve: (JSONObject) -> Unit, timeout: Long = DEFAULT_TIMEOUT
    ) {
        i(tag, "Starting loading json object from $url")
        val request = JsonObjectRequest(Request.Method.POST, url, params, {
            i(tag, "Successfully loaded")
            resolve(it)
        }, {
            i(tag, "Bad result of loading")
            i(tag, it.toString())
            Handler().postDelayed({
                infinitePostJsonObjectRequest(url, params, resolve, timeout)
            }, timeout)
        })
        requestQueue.add(request)
    }

    fun infinitePostStringRequest(
        url: String, params: JSONObject,
        resolve: (String) -> Unit, timeout: Long = DEFAULT_TIMEOUT
    ) {
        i(tag, "Starting loading string from $url")
        val request = object : StringRequest(Request.Method.POST, url, {
            i(tag, "Successfully loaded string from $url")
            resolve(it)
        }, {
            i(tag, "Error happened while loading string from $url")
            Handler().postDelayed({
                infinitePostStringRequest(url, params, resolve, timeout)
            }, timeout)
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return params.toString().toByteArray()
            }
        }
        requestQueue.add(request)
    }
}