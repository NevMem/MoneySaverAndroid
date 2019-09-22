package com.nevmem.moneysaver.app.room.dao

import androidx.room.*
import com.nevmem.moneysaver.common.data.Record

@Dao
interface HistoryDao {
    @Insert
    fun insert(record: Record)

    @Update
    fun update(record: Record)

    @Delete
    fun delete(record: Record)

    @Query("SELECT * FROM record ORDER BY timestamp DESC")
    fun loadAll(): List<Record>

    @Query("SELECT * FROM record WHERE id = :historyId LIMIT 1")
    fun findById(historyId: String): Record?
}