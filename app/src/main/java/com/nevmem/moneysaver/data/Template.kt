package com.nevmem.moneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nevmem.moneysaver.room.entity.StoredTemplate
import java.util.ArrayList

class Template {
    var name: String
    var value: Double
    var tag: String
    var wallet: String
    var id: String

    var sending = false
    var success = false
    var error: String? = null

    constructor(name: String, value: Double, tag: String, wallet: String, id: String) {
        this.name = name
        this.value = value
        this.tag = tag
        this.wallet = wallet
        this.id = id
    }

    constructor(stored: StoredTemplate) : this(stored.name, stored.value, stored.tag, stored.wallet, stored.id)
}
