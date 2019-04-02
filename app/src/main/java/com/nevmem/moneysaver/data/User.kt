package com.nevmem.moneysaver.data

import android.content.Context
import com.nevmem.moneysaver.exceptions.UserCredentialsNotFound

class User(var login: String, var token: String, var first_name: String, var last_name: String) {
    override fun toString(): String {
        return "${this.login}\n${this.first_name}\n${this.last_name}\n${this.token}"
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
            editor.putString("user.first_name", user.first_name)
            editor.putString("user.last_name", user.last_name)
            editor.commit()
        }

        fun clearCredentials(context: Context) {
            val sharedPrefs = context.getSharedPreferences("com.nevmem.glob", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                remove("user.login")
                remove("user.token")
                remove("user.first_name")
                remove("user.last_name")
                commit()
            }
        }
    }
}
