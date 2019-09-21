package com.nevmem.moneysaver.app.room.dao

import androidx.room.*
import com.nevmem.moneysaver.app.data.MonthDescription

@Dao
interface MonthDescriptionDao {
    @Query("SELECT * FROM monthdescription WHERE monthId = :monthId LIMIT 1")
    fun getByMonthID(monthId: String): MonthDescription?

    @Insert
    fun insert(monthDescription: MonthDescription)

    @Update
    fun update(monthDescription: MonthDescription)

    @Delete
    fun delete(monthDescription: MonthDescription)

    @Query("SELECT * FROM monthdescription ORDER BY monthTimestamp DESC LIMIT 1")
    fun getLastMonth(): MonthDescription?

    @Query("SELECT * FROM monthdescription ORDER BY monthTimestamp")
    fun getAll(): List<MonthDescription>
}