package com.nevmem.moneysaver.common.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.common.converters.HistoryConverter
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (name != other.name) return false
        if (value != other.value) return false
        if (tag != other.tag) return false
        if (wallet != other.wallet) return false
        if (date != other.date) return false
        if (id != other.id) return false
        if (daily != other.daily) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + wallet.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + daily.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
