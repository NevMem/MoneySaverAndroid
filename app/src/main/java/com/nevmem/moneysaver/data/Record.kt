package com.nevmem.moneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.room.converters.HistoryConverter
import org.json.JSONObject

@Entity
class Record {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
    var name: String = ""
    var value: Double = 0.toDouble()
    var tag: String = ""
    var wallet: String = "unknown wallet"
    @TypeConverters(HistoryConverter::class)
    var date: RecordDate = RecordDate()
    var id: String = ""
    var daily: Boolean = true
    var timestamp: Long = 0

    override fun toString(): String {
        return "{$name $value [$id]}"
    }

    fun updateBy(other: Record) {
        this.name = other.name
        this.value = other.value
        this.timestamp = other.timestamp
        this.tag = other.tag
        this.daily = other.daily
        this.wallet = other.wallet
        this.date = other.date
    }

    fun injectJson(json: JSONObject) {
        json.put("name", name)
        json.put("value", value)
        json.put("tag", tag)
        json.put("wallet", wallet)
        json.put("daily", daily)
        json.put("id", id)
        val dateJson = JSONObject()
        date.injectJson(dateJson)
        json.put("date", dateJson)
    }
}
