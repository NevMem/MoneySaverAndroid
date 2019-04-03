package com.nevmem.moneysaver.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository
@Inject constructor(var networkQueue: NetworkQueue, var appDatabase: AppDatabase,
                    var executor: Executor, var userHolder: UserHolder) {
    private var tag = "IREP"
    var info = MutableLiveData<Info>(Info())
    var error = MutableLiveData<String>("")
    var success = MutableLiveData<String>("")
    var loading = MutableLiveData<Boolean>(false)

    init {
        tryUpdate()
    }

    fun tryUpdate() {
        loadFromDatabase()
        loadFromNet()
    }

    private fun loadFromDatabase() {
        executor.execute {
            with(appDatabase.infoDao()) {
                val saved = get()
                if (saved != null) {
                    info.postValue(saved)
                }
            }
        }
    }

    private fun resolveConflicts(info: Info) {
        executor.execute {
            with(appDatabase.infoDao()) {
                val saved = get()
                if (saved != null) {
                    info.uid = saved.uid
                    update(info)
                } else {
                    info.uid = 0
                    insert(info)
                }
            }
        }
    }

    private fun loadFromNet() {
        val params = userHolder.credentialsJson()
        params.put("info7", true)
        params.put("info30", true)
        params.put("daysDescription", true)
        params.put("months", true)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiInfo, params, {
            i(tag, it.toString())
            if (it.has("type")) {
                val type = it.getString("type")
                if (type == "ok") {
                    val info = Info()
                    info.fromJSON(it.getJSONObject("info"))
                    resolveConflicts(info)
                    loadFromDatabase()
                } else if (type == "error") {
                    error.postValue(it.getString("error"))
                } else {
                    error.postValue("Server response has unknown format")
                }
            } else {
                error.postValue("Sever response has unknown format")
            }
        })
    }
}