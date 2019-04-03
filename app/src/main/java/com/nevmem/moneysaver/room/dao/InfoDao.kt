package com.nevmem.moneysaver.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nevmem.moneysaver.data.Info

@Dao
interface InfoDao {
    @Query("SELECT * FROM info LIMIT 1")
    fun get(): Info?

    @Insert
    fun insert(info: Info)

    @Update
    fun update(info: Info)
}