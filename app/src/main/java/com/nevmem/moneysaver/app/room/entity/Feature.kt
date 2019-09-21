package com.nevmem.moneysaver.app.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Feature {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    var featureName: String

    constructor(name: String) {
        featureName = name
    }

    constructor(uid: Int, featureName: String) {
        this.uid = uid
        this.featureName = featureName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feature

        if (featureName != other.featureName) return false

        return true
    }

    override fun hashCode(): Int {
        return featureName.hashCode()
    }
}