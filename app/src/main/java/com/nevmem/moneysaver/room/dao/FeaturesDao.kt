package com.nevmem.moneysaver.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nevmem.moneysaver.room.entity.Feature

@Dao
interface FeaturesDao {
    @Insert
    fun insert(feature: Feature)

    @Query("SELECT * FROM feature")
    fun loadAll(): List<Feature>

    @Delete
    fun delete(feature: Feature)
}