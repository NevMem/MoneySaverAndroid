package com.nevmem.moneysaver.app.room.dao

import androidx.room.*
import com.nevmem.moneysaver.app.room.entity.StoredTemplate

@Dao
interface TemplateDao {
    @Query("SELECT * FROM storedtemplate")
    fun loadAll(): List<StoredTemplate>

    @Insert
    fun insert(vararg templates: StoredTemplate)

    @Update
    fun update(vararg templates: StoredTemplate)

    @Query("SELECT * FROM storedtemplate WHERE id = :id LIMIT 1")
    fun findByIdOne(id: String): StoredTemplate?

    @Delete
    fun delete(template: StoredTemplate)

    @Query("DELETE FROM storedtemplate")
    fun deleteAll()
}