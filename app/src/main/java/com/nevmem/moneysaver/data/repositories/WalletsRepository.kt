package com.nevmem.moneysaver.data.repositories

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

    fun tryLoad() {
        val params = userHolder.credentialsJson()
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiWallets, params, {
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    val array = it.getJSONArray("data")
                    val list = ArrayList<Wallet>()
                    for (index in 0 until(list.size))
                        list.add(Wallet(array.getString(index)))
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

    private fun resolveConflicts(wallets: List<Wallet>) {
        executor.execute {
            with (appDatabase.walletsDao()) {
                wallets.forEach {
                    val inDatabase = findByName(it.name)
                    if (inDatabase == null) {
                        insert(it)
                    }
                }
            }
        }
    }
}