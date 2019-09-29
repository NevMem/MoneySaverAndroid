package com.nevmem.moneysaver.auth

import android.content.Context
import com.nevmem.moneysaver.common.exceptions.UserCredentialsNotFound

class User(var login: String, var token: String, var firstName: String, var lastName: String) {
    override fun toString(): String {
        return "${this.login}\n${this.firstName}\n${this.lastName}\n${this.token}"
    }

    companion object {
        fun loadUserCredentials(context: Context): User {
            val sharedPrefs = context.getSharedPreferences("com.nevmem.glob", Context.MODE_PRIVATE)
            val login = sharedPrefs.getString("user.login", "empty")!!
            val token = sharedPrefs.getString("user.token", "empty")!!
            val firstName = sharedPrefs.getString("user.first_name", "empty")!!
            val lastName = sharedPrefs.getString("user.last_name", "empty")!!
            if (login == "empty" || token == "empty" || firstName == "empty" || lastName == "empty")
                throw UserCredentialsNotFound()
            return User(login, token, firstName, lastName)
        }

        fun saveUserCredentials(context: Context, user: User) {
            val sharedPrefs = context.getSharedPreferences("com.nevmem.glob", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("user.login", user.login)
            editor.putString("user.token", user.token)
            editor.putString("user.first_name", user.firstName)
            editor.putString("user.last_name", user.lastName)
            editor.apply()
        }

        fun clearCredentials(context: Context) {
            val sharedPrefs = context.getSharedPreferences("com.nevmem.glob", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                remove("user.login")
                remove("user.token")
                remove("user.first_name")
                remove("user.last_name")
                apply()
            }
        }
    }
}
