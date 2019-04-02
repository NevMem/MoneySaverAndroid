package com.nevmem.moneysaver.data

import android.content.Context
import org.json.JSONObject

class UserHolder(var context: Context) {
    var user: User = User.loadUserCredentials(context)

    fun credentialsJson(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }
}