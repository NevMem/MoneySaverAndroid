package com.nevmem.moneysaver.app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nevmem.moneysaver.app.data.Template

@Entity
class StoredTemplate {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
    var name: String
    var value: Double
    var wallet: String
    var tag: String
    var id: String

    constructor(name: String, value: Double, wallet: String, tag: String, id: String) {
        this.name = name
        this.value = value
        this.wallet = wallet
        this.tag = tag
        this.id = id
    }

    constructor(template: Template) : this(template.name, template.value, template.wallet, template.tag, template.id)

    fun updateBy(template: Template) {
        name = template.name
        wallet = template.wallet
        value = template.value
        id = template.id
        tag = template.tag
    }
}