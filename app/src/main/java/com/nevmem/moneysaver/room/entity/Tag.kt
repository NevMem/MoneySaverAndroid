package com.nevmem.moneysaver.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Tag(var name: String = "") {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}