package com.nevmem.moneysaver.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.room.dao.HistoryDao
import com.nevmem.moneysaver.room.dao.TagsDao
import com.nevmem.moneysaver.room.dao.TemplateDao
import com.nevmem.moneysaver.room.dao.WalletsDao
import com.nevmem.moneysaver.room.entity.StoredTemplate
import com.nevmem.moneysaver.room.entity.Tag
import com.nevmem.moneysaver.room.entity.Wallet

@Database(entities = [StoredTemplate::class, Record::class, Wallet::class, Tag::class], version = 13)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
    abstract fun historyDao(): HistoryDao
    abstract fun walletsDao(): WalletsDao
    abstract fun tagsDao(): TagsDao
}