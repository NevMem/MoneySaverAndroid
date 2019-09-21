package com.nevmem.moneysaver.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.app.room.converters.MonthDescriptionTypeConverters

@Entity
class MonthDescription {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    var monthId: String = ""
    var total: Double = 0.0
    var totalDaily: Double = 0.0
    var average: Double = 0.0
    var averageDaily: Double = 0.0
    var monthTimestamp: Int = 0

    @TypeConverters(MonthDescriptionTypeConverters::class)
    var byTagTotal: HashMap<String, Double> = HashMap()

    @TypeConverters(MonthDescriptionTypeConverters::class)
    var byTagDaily: HashMap<String, Double> = HashMap()
}