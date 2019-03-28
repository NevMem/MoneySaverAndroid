package com.nevmem.moneysaver.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.room.dao.TemplateDao
import com.nevmem.moneysaver.room.entity.StoredTemplate

@Database(entities = [StoredTemplate::class], version = 8)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
}