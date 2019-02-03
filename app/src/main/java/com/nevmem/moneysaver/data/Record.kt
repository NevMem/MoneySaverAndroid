package com.nevmem.moneysaver.data

import java.util.ArrayList

class Record {
    var name: String
    var value: Double = 0.toDouble()
    var tags: ArrayList<String>
    var wallet: String = "unknown wallet"
    var date: RecordDate
    var id: String

    constructor() {
        name = "undefined"
        tags = ArrayList()
        date = RecordDate()
        id = ""
    }

    constructor(name: String) {
        this.name = name
        tags = ArrayList()
        date = RecordDate()
        id = ""
    }

    constructor(name: String, value: Int) {
        this.name = name
        this.value = value.toDouble()
        tags = ArrayList()
        date = RecordDate()
        id = ""
    }

    override fun toString(): String {
        return "{$name $value [$id]}"
    }
}
