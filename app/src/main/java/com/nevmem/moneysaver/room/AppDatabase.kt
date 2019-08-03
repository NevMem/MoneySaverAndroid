package com.nevmem.moneysaver.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.room.dao.*
import com.nevmem.moneysaver.room.entity.Feature
import com.nevmem.moneysaver.room.entity.StoredTemplate
import com.nevmem.moneysaver.room.entity.Tag
import com.nevmem.moneysaver.room.entity.Wallet

@Database(
    entities = [
        StoredTemplate::class,
        Record::class,
        Wallet::class,
        Tag::class,
        Info::class,
        MonthDescription::class,
        Feature::class],
    exportSchema = false,
    version = 19
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
    abstract fun historyDao(): HistoryDao
    abstract fun walletsDao(): WalletsDao
    abstract fun tagsDao(): TagsDao
    abstract fun infoDao(): InfoDao
    abstract fun monthDescriptionDao(): MonthDescriptionDao
    abstract fun featuresDao(): FeaturesDao
}