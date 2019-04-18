package com.nevmem.moneysaver.data

import org.json.JSONObject

interface NetworkQueueBase {
    companion object {
        const val DEFAULT_TIMEOUT: Long = 5000
    }

    /**
     * This function is used to perform infinite request with post params [params]
     * Loads JSONObject from [url]
     * @param url: URL to request
     * @param params: JSON Post request params
     * @param resolve: callback which will be called after successfully performed request
     * @param timeout: time delay between two requests
     * @return request: object which represents api for this request
     */
    fun infinitePostJsonObjectRequest(
        url: String, params: JSONObject,
        resolve: (JSONObject) -> Unit, timeout: Long = DEFAULT_TIMEOUT
    ): RequestBase<JSONObject>

    /**
     * Same as previous function but you have subscribe on success event by yourself
     */
    fun infinitePostJsonObjectRequest(
        url: String, params: JSONObject, timeout: Long = DEFAULT_TIMEOUT
    ): RequestBase<JSONObject>

    /**
     * Same as infinitePostJsonObjectRequest but loads string
     */
    fun infinitePostStringRequest(
        url: String, params: JSONObject,
        resolve: (String) -> Unit, timeout: Long = DEFAULT_TIMEOUT
    ): RequestBase<String>

    /**
     * Same as previous function but you have subscribe on success event by yourself
     */
    fun infinitePostStringRequest(
        url: String, params: JSONObject, timeout: Long = DEFAULT_TIMEOUT
    ): RequestBase<String>
}