package com.nevmem.moneysaver.data.repositories

import android.os.Handler
import android.os.Looper
import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordDate
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import org.json.JSONArray
import java.lang.Math.abs
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private var networkQueue: NetworkQueue,
    private var userHolder: UserHolder,
    private var executor: Executor,
    private var appDatabase: AppDatabase
) {
    val loading = MutableLiveData<Boolean>(false)
    val history = MutableLiveData<ArrayList<Record>>(ArrayList())
    val error = MutableLiveData<String>("")

    init {
        i("HREP", "Init was called")
        loadFromDatabase()
        loadFromNet()
    }

    public fun tryUpdate() {
        loadFromNet()
    }

    private fun resolveConflicts(data: ArrayList<Record>) {
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
                "HREP",
                "All conflicts was resolved\ninserted: $inserted items, " +
                        "updated $updated items, removed $removed items"
            )
            Handler(Looper.getMainLooper()).post {
                history.postValue(data)
                loading.value = false
            }
        }
    }

    fun delete(record: Record) {
        val params = userHolder.credentialsJson()
        i("HREP", record.id)
        params.put("record_id", record.id)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiDeleteRecord, params, {
            i("HREP", "Received $it")
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    executor.execute {
                        with(appDatabase.historyDao()) {
                            i("HREP", "Delete request ${record.uid}")
                            val toDelete = findById(record.id)
                            if (toDelete != null)
                                delete(toDelete)
                            history.postValue(ArrayList(loadAll()))
                        }
                    }
                } else if (it.getString("type") == "error") {
                    error.postValue(it.getString("error"))
                } else {
                    error.postValue("Server response has unknown format")
                }
            }
        })
    }

    private fun parseHistory(array: JSONArray): ArrayList<Record> {
        val result = ArrayList<Record>()
        for (index in 0 until (array.length())) {
            try { // TODO: (refactor)
                val json = array.getJSONObject(index)
                val record = Record()
                record.name = json.getString("name")
                record.value = abs(json.getDouble("value"))
                record.tag = json.getString("tag")
                record.wallet = json.getString("wallet")
                record.daily = json.getBoolean("daily")
                record.id = json.getString("_id")
                record.date = RecordDate.fromJSON(json.getJSONObject("date"))
                record.timestamp = json.getLong("timestamp")

                result.add(record)
            } catch (_: Exception) {
            }
        }
        return result
    }

    private fun loadFromNet() {
        loading.value = true
        i("HREP", "Requesting")
        error.value = ""
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiHistory, userHolder.credentialsJson(), {
            i("HREP", "${it.toString()}")
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    val parsed = parseHistory(it.getJSONArray("data"))
                    resolveConflicts(parsed)
                } else if (it.getString("type") == "error") {
                    error.value = it.getString("type")
                } else {
                    error.value = "Server response has unknown format"
                }
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
}