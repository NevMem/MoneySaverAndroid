package com.nevmem.moneysaver.data

import android.content.Context
import org.json.JSONObject

class UserHolder {
    lateinit var user: User
    var context: Context

    constructor(context: Context) {
        this.context = context
        user = User.loadUserCredintials(context)
    }

    fun credentialsJson(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }
}