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
        url: String, params: JSONObject, timeout: Long, savedRequest: RequestBase<JSONObject>?
    ): RequestBase<JSONObject> {
        i(tag, "Starting loading json object from $url")
        val request = savedRequest ?: Request()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params, {
            i(tag, "Successfully loaded json object from $url")
            if (!request.isCanceled()) {
                i(tag, "Resolving")
                request.resolve(it)
            } else {
                i(tag, "Request was canceled")
            }
        }, {
            i(tag, "Bad result of loading from $url")
            i(tag, it.toString())
            if (!request.isCanceled()) {
                Handler().postDelayed({
                    infinitePostJsonObjectRequest(url, params, timeout, request)
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
        url: String, params: JSONObject, timeout: Long, savedRequest: RequestBase<String>?
    ): RequestBase<String> {
        val request = savedRequest ?: Request<String>()
        val stringRequest = object : StringRequest(Method.POST, url, {
            if (!request.isCanceled()) {
                request.resolve(it)
            }
        }, {
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