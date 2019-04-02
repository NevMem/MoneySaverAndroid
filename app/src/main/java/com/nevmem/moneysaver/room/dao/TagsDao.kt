package com.nevmem.moneysaver.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nevmem.moneysaver.room.entity.Tag

@Dao
interface TagsDao {
    @Query("SELECT * FROM tag")
    fun getAll(): List<Tag>

    @Insert
    fun insert(tag: Tag)

    @Update
    fun update(tag: Tag)

    @Query("SELECT * FROM tag WHERE name = :name")
    fun findByName(name: String): Tag?
}
