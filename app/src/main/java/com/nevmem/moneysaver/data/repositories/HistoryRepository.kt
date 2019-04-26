package com.nevmem.moneysaver.data.repositories

import android.os.Handler
import android.os.Looper
import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.util.*
import com.nevmem.moneysaver.room.AppDatabase
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private var networkQueue: NetworkQueueBase,
    private var userHolder: UserHolder,
    private var executor: Executor,
    private var appDatabase: AppDatabase
) {
    val loading = MutableLiveData<Boolean>(false)
    val history = MutableLiveData<ArrayList<Record>>(ArrayList())
    val error = MutableLiveData<String>("")

    val editingState = MutableLiveData<RequestState>(NoneState)

    private val tag = "H_REP"

    init {
        i(tag, "Init was called")
        loadFromDatabase()
        loadFromNet()
    }

    fun tryUpdate() {
        loadFromNet()
    }

    private fun resolveConflicts(data: ArrayList<Record>) {
        i(tag, "Resolving conflicts")
        executor.execute {
            var inserted = 0
            var updated = 0
            var removed = 0
            val ids = HashSet<String>()
            with(appDatabase.historyDao()) {
                data.forEach {
                    val inDatabase = findById(it.id)
                    if (inDatabase == null) {
                        insert(it)
                        inserted += 1
                    } else {
                        inDatabase.updateBy(it)
                        update(inDatabase)
                        updated += 1
                    }
                    ids.add(it.id)
                }
                loadAll().forEach {
                    if (!ids.contains(it.id)) {
                        delete(it)
                        removed += 1
                    }
                }
            }
            i(
                tag,
                "All conflicts was resolved\ninserted: $inserted items, " +
                        "updated $updated items, removed $removed items"
            )
            Handler(Looper.getMainLooper()).post {
                i(tag, "Posting data and loading false to live data")
                history.postValue(data)
                loading.postValue(false)
            }
        }
    }

    fun delete(record: Record) {
        val params = userHolder.credentialsJson()
        i(tag, record.id)
        params.put("record_id", record.id)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiDeleteRecord, params, {
            i(tag, "Received $it")
            if (it.has("type")) {
                when {
                    it.getString("type") == "ok" -> executor.execute {
                        with(appDatabase.historyDao()) {
                            i(tag, "Delete request ${record.uid}")
                            val toDelete = findById(record.id)
                            if (toDelete != null)
                                delete(toDelete)
                            history.postValue(ArrayList(loadAll()))
                        }
                    }
                    it.getString("type") == "error" -> error.postValue(it.getString("error"))
                    else -> error.postValue("Server response has unknown format")
                }
            } else {
                error.postValue("Server response has unknown format")
                loading.postValue(false)
            }
        })
    }

    private fun loadFromNet() {
        loading.postValue(true)
        i(tag, "Requesting")
        error.postValue("")
        val request =
            networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiHistory, userHolder.credentialsJson())
        request.success {
            val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(it)
            i(tag, "Received some data")
            if (parsed is ParseError) {
                i(tag, "Error ${parsed.reason}")
                error.postValue(parsed.reason)
            } else if (parsed is ParsedValue<*>) {
                i(tag, "Parsed some info")
                resolveConflicts(
                    try {
                        parsed.parsed as ArrayList<Record>
                    } catch (e: ClassCastException) {
                        ArrayList<Record>()
                    }
                )
            }
        }
    }

    fun addRecord(record: Record, cb: (String?) -> Unit) {
        val params = userHolder.credentialsJson()
        params.put("name", record.name)
        params.put("value", record.value)
        params.put("wallet", record.wallet)
        params.put("daily", record.daily)
        params.put("tag", record.tag)
        params.put("date", record.date.toJSON())
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiAdd, params, {
            when {
                it.has("type") -> when {
                    it.getString("type") == "ok" -> cb(null)
                    it.getString("type") == "error" -> cb(it.getString("error"))
                    else -> cb("Server response has unknown format")
                }
                else -> cb("Server response has unknown format")
            }
        })
    }

    private fun loadFromDatabase() {
        executor.execute {
            val list: ArrayList<Record> = ArrayList(appDatabase.historyDao().loadAll())
            Handler(Looper.getMainLooper()).post {
                history.value = list
            }
        }
    }

    fun getRecordOnIndex(index: Int): Record {
        val buffer = history.value
        return when {
            buffer != null && index <= buffer.size -> buffer[index]
            else -> Record()
        }
    }

    fun editRecord(record: Record) {
        val params = userHolder.credentialsJson()
        record.injectJson(params)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiEdit, params)
        editingState.postValue(LoadingState)
        request.success {
            when (val parsed = HistoryRepositoryParsers.parseServerEditRequest(it)) {
                is ParseError -> editingState.postValue(ErrorState(parsed.reason))
                is ParsedValue<*> -> editingState.postValue(SuccessState("Successfully changed"))
            }
        }
    }
}