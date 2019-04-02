package com.nevmem.moneysaver.data.repositories

import android.os.Handler
import android.os.Looper
import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import com.nevmem.moneysaver.room.entity.Wallet
import java.util.concurrent.Executor
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class WalletsRepository @Inject constructor(
    var appDatabase: AppDatabase,
    var networkQueue: NetworkQueue,
    var userHolder: UserHolder,
    var executor: Executor
) {
    var loading = MutableLiveData<Boolean>(true)
    var error = MutableLiveData<String>("")
    var wallets = MutableLiveData<List<Wallet>>(ArrayList())

    private fun loadFromNet() {
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiWallets, userHolder.credentialsJson(), {
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    val array = it.getJSONArray("data")
                    val list = ArrayList<Wallet>()
                    for (index in 0 until(array.length()))
                        list.add(Wallet(array.getString(index)))
                    i("WREP", "From net received ${list.size} elements")
                    resolveConflicts(list)
                } else if (it.getString("type") == "error") {
                    error.postValue(it.getString("type"))
                } else {
                    error.postValue("Server error has unknown format")
                }
            } else {
                error.postValue("Server error has unknown format")
            }
        })
    }

    private fun loadFromDatabase() {
        executor.execute {
            val fromDatabase = appDatabase.walletsDao().get()
            i("WREP", "Loaded ${fromDatabase.size}")
            Handler(Looper.getMainLooper()).post {
                wallets.postValue(fromDatabase)
            }
        }
    }

    fun tryUpdate() {
        loadFromDatabase()
        loadFromNet()
    }

    private fun resolveConflicts(values: List<Wallet>) {
        executor.execute {
            with (appDatabase.walletsDao()) {
                values.forEach {
                    val inDatabase = findByName(it.name)
                    if (inDatabase == null) {
                        i("WREP", "Inserting")
                        insert(it)
                    }
                }
            }
            loadFromDatabase()
        }
    }

    fun getWalletsAsList(): List<String> {
        val result = ArrayList<String>()
        wallets.value?.forEach {
            result.add(it.name)
        }
        return result
    }
}