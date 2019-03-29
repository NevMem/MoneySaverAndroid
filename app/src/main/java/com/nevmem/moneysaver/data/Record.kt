package com.nevmem.moneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.room.converters.HistoryConverter
import org.json.JSONArray
import java.util.ArrayList

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

    /* constructor() {
        name = "undefined"
        tag = ""
        date = RecordDate()
        id = ""
        daily = false
    }

    constructor(
        uid: Int,
        name: String,
        value: Double,
        tag: String,
        wallet: String,
        date: RecordDate,
        id: String,
        daily: Boolean
    ) {
        this.uid = uid
        this.name = name
        this.value = value
        this.tag = tag
        this.wallet = wallet
        this.date = date
        this.id = id
        this.daily = daily
    }

    constructor(name: String): this() {
        this.name = name
    }

    constructor(name: String, value: Int): this(name) {
        this.value = value.toDouble()
    } */

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
}
