package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.*
import com.nevmem.moneysaver.data.Record

class App() : Application() {
    init {
        i("APP_CLASS", "App() init method was called")
    }

    var records: ArrayList<Record> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        i("APP_CLASS", "onCreate method was called")
    }

    fun clearRecords() {
        records.clear()
        i("APP_CLASS", "records array was cleared")
    }


    fun saveRecords(from: ArrayList<Record>) {
        for (index in 0 until(from.size))
            records.add(from[index])
        i("APP_CLASS", "Saved ${from.size} records")
    }
}
