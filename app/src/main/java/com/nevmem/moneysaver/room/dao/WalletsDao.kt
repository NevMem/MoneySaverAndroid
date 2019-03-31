package com.nevmem.moneysaver.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nevmem.moneysaver.room.entity.Wallet

@Dao
interface WalletsDao {
    @Insert
    fun insert(wallet: Wallet)

    @Query("SELECT * FROM wallet")
    fun get(): List<Wallet>
}