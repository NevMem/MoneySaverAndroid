package com.nevmem.moneysaver.data

import dagger.Binds
import org.json.JSONObject

interface NetworkQueueBase {
    companion object {
        const val DEFAULT_TIMEOUT: Long = 5000
    }

    fun infinitePostJsonObjectRequest(url: String, params: JSONObject,
                                      resolve: (JSONObject) -> Unit, timeout: Long = DEFAULT_TIMEOUT)
    fun infinitePostStringRequest(url: String, params: JSONObject,
                                  resolve: (String) -> Unit, timeout: Long = DEFAULT_TIMEOUT)
}