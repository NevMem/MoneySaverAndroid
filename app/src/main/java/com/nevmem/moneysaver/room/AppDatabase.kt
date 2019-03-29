package com.nevmem.moneysaver.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.room.dao.HistoryDao
import com.nevmem.moneysaver.room.dao.TemplateDao
import com.nevmem.moneysaver.room.entity.StoredTemplate

@Database(entities = [StoredTemplate::class, Record::class], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
    abstract fun historyDao(): HistoryDao
}