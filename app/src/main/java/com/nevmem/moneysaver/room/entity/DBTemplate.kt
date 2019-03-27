package com.nevmem.moneysaver.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DBTemplate(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "wallet") var wallet: String,
    @ColumnInfo(name = "value") var value: Double,
    @ColumnInfo(name = "tag") var tag: String
)