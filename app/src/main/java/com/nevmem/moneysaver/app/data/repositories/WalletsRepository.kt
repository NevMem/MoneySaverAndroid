package com.nevmem.moneysaver.app.data.repositories

import android.os.Handler
import android.os.Looper
import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.util.*
import com.nevmem.moneysaver.app.room.AppDatabase
import com.nevmem.moneysaver.app.room.entity.Wallet
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletsRepository @Inject constructor(
    var appDatabase: AppDatabase,
    var networkQueue: com.nevmem.moneysaver.network.NetworkQueue,
    var userHolder: com.nevmem.moneysaver.auth.UserHolder,
    var executor: Executor
) {
    var loading = MutableLiveData<Boolean>(true)
    var error = MutableLiveData<String>("")
    var wallets = MutableLiveData<List<Wallet>>(ArrayList())

    var addingState = MutableLiveData<RequestState>(NoneState)

    private var tag = "WALLETS_REPOSITORY"

    init {
        i(tag, "init")
        loadFromDatabase()
        tryUpdate()
    }

    private fun loadFromNet() {
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiWallets, userHolder.credentialsJson(), {
            if (it.has("type")) {
                when {
                    it.getString("type") == "ok" -> {
                        val array = it.getJSONArray("data")
                        val list = ArrayList<Wallet>()
                        for (index in 0 until(array.length()))
                            list.add(Wallet(array.getString(index)))
                        i(tag, "From net received ${list.size} elements")
                        resolveConflicts(list)
                    }
                    it.getString("type") == "error" -> error.postValue(it.getString("type"))
                    else -> error.postValue("Server error has unknown format")
                }
            } else {
                error.postValue("Server error has unknown format")
            }
        })
    }

    private fun loadFromDatabase() {
        executor.execute {
            val fromDatabase = appDatabase.walletsDao().get()
            i(tag, "Loaded ${fromDatabase.size}")
            Handler(Looper.getMainLooper()).post {
                wallets.postValue(fromDatabase)
            }
        }
    }

    fun tryUpdate() {
        loadFromNet()
    }

    private fun resolveConflicts(values: List<Wallet>) {
        executor.execute {
            with (appDatabase.walletsDao()) {
                val names = HashSet<String>()
                values.forEach {
                    names.add(it.name)
                    val inDatabase = findByName(it.name)
                    if (inDatabase == null) {
                        i(tag, "Inserting")
                        insert(it)
                    }
                }
                get().forEach {
                    if (!names.contains(it.name))
                        delete(it)
                }
            }
            loadFromDatabase()
        }
    }

    fun addWallet(wallet: String) {
        addingState.postValue(LoadingState)
        val params = userHolder.credentialsJson()
        params.put("walletName", wallet)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiAddWallet, params)
        request.success {
            when (val parsed = WalletsRepositoryParsers.parseAddWalletRequest(it)) {
                is ParsedValue<*> -> {
                    addingState.postValue(SuccessState(parsed.parsed as String))
                }
                is ParseError -> {
                    addingState.postValue(ErrorState(parsed.reason))
                }
            }
            tryUpdate()
        }
    }

    fun receivedAddingError() {
        addingState.postValue(NoneState)
    }

    fun delete(walletName: String): MutableLiveData<RequestState> {
        val state = MutableLiveData<RequestState>(LoadingState)
        val params = userHolder.credentialsJson()
        params.put("walletName", walletName)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiRemoveWallet, params)
        request.success {
            when (val parseResult = TagsRepositoryParsers.parseRemoveTagResponse(it)) {
                is ParseError -> {
                    state.postValue(ErrorState(parseResult.reason))
                }
                is ParsedValue<*> -> {
                    state.postValue(SuccessState())
                }
            }
        }
        return state
    }
}