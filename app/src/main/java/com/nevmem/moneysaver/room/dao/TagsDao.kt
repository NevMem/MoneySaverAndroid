package com.nevmem.moneysaver.room.dao

import androidx.room.*
import com.nevmem.moneysaver.room.entity.Tag

@Dao
interface TagsDao {
    @Query("SELECT * FROM tag")
    fun getAll(): List<Tag>

    @Insert
    fun insert(tag: Tag)

    @Update
    fun update(tag: Tag)

    @Delete
    fun delete(tag: Tag)

    @Query("SELECT * FROM tag WHERE name = :name")
    fun findByName(name: String): Tag?
}
