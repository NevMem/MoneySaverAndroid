package com.nevmem.moneysaver.app.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.app.room.dao.*
import com.nevmem.moneysaver.app.room.entity.Feature
import com.nevmem.moneysaver.app.room.entity.StoredTemplate
import com.nevmem.moneysaver.common.data.Tag
import com.nevmem.moneysaver.app.room.entity.Wallet

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
    version = 20
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