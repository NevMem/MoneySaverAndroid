package com.nevmem.moneysaver.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.room.dao.DBTemplateDao
import com.nevmem.moneysaver.room.entity.DBTemplate

@Database(entities = arrayOf(DBTemplate::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): DBTemplateDao
}