package com.nevmem.moneysaver.app.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nevmem.moneysaver.app.room.entity.Wallet

@Dao
interface WalletsDao {
    @Insert
    fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallet")
    fun get(): List<Wallet>

    @Query("SELECT * FROM wallet WHERE name = :name LIMIT 1")
    fun findByName(name: String): Wallet?

    @Delete
    fun delete(wallet: Wallet)
}