package com.nevmem.moneysaver.auth

import android.content.Context
import com.nevmem.moneysaver.common.exceptions.UserCredentialsNotFound
import org.json.JSONObject

class UserHolder(var context: Context) {
    var user: User
    var ready = false

    init {
        try {
            user = User.loadUserCredentials(context)
            ready = true
        } catch (_: UserCredentialsNotFound) {
            user = User("", "", "", "")
        }
    }

    fun initializeByUser(user: User) {
        this.user = user
        User.saveUserCredentials(context, user)
        ready = true
    }

    fun initializeByJson(json: JSONObject) {
        val login = json.getString("login")
        val token = json.getString("token")
        val firstName = json.getString("first_name")
        val lastName = json.getString("last_name")
        user = User(login, token, firstName, lastName)
        User.saveUserCredentials(context, user)
        ready = true
    }

    fun credentialsJson(): JSONObject {
        val json = JSONObject()
        json.put("login", user.login)
        json.put("token", user.token)
        return json
    }
}