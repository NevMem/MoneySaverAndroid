package com.nevmem.moneysaver.app.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nevmem.moneysaver.app.data.Info

@Dao
interface InfoDao {
    @Query("SELECT * FROM info LIMIT 1")
    fun get(): Info?

    @Insert
    fun insert(info: Info)

    @Update
    fun update(info: Info)
}