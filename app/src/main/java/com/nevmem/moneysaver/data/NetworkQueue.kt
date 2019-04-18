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

class NetworkQueue(ctx: Context) : NetworkQueueBase {
    private var volleyRequestQueue: RequestQueue = Volley.newRequestQueue(ctx)
    private val tag = "NET_QUEUE"

    init {
        i(tag, "NetworkQueue constructor was called")
    }

    override fun infinitePostJsonObjectRequest(
        url: String, params: JSONObject, timeout: Long
    ): RequestBase<JSONObject> {
        i(tag, "Starting loading json object from $url")
        val request = Request<JSONObject>()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params, {
            i(tag, "Successfully loaded")
            if (!request.isCanceled()) {
                request.resolve(it)
            }
        }, {
            i(tag, "Bad result of loading")
            i(tag, it.toString())
            if (!request.isCanceled()) {
                Handler().postDelayed({
                    infinitePostJsonObjectRequest(url, params, timeout)
                }, timeout)
            }
        })
        volleyRequestQueue.add(jsonObjectRequest)
        return request
    }

    override fun infinitePostJsonObjectRequest(
        url: String,
        params: JSONObject,
        resolve: (JSONObject) -> Unit,
        timeout: Long
    ): RequestBase<JSONObject> {
        val request = infinitePostJsonObjectRequest(url, params, timeout)
        request.success(resolve)
        return request
    }

    override fun infinitePostStringRequest(
        url: String, params: JSONObject, timeout: Long
    ): RequestBase<String> {
        i(tag, "Starting loading string from $url")
        val request = Request<String>()
        val stringRequest = object : StringRequest(Method.POST, url, {
            i(tag, "Successfully loaded string from $url")
            if (!request.isCanceled()) {
                request.resolve(it)
            }
        }, {
            i(tag, "Error happened while loading string from $url")
            if (!request.isCanceled()) {
                Handler().postDelayed({
                    infinitePostStringRequest(url, params, timeout)
                }, timeout)
            }
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                return params.toString().toByteArray()
            }
        }
        volleyRequestQueue.add(stringRequest)
        return request
    }

    override fun infinitePostStringRequest(
        url: String,
        params: JSONObject,
        resolve: (String) -> Unit,
        timeout: Long
    ): RequestBase<String> {
        val request = infinitePostStringRequest(url, params, timeout)
        request.success(resolve)
        return request
    }
}