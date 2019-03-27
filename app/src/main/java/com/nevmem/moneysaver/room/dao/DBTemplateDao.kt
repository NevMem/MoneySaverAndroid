package com.nevmem.moneysaver.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nevmem.moneysaver.room.entity.DBTemplate

@Dao
interface DBTemplateDao {
    @Query("SELECT * FROM dbtemplate")
    fun loadAll(): List<DBTemplate>

    @Insert
    fun addTemplates(vararg templates: DBTemplate)

    @Delete
    fun deleteTemplate(template: DBTemplate)
}