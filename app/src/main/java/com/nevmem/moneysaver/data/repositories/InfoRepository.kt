package com.nevmem.moneysaver.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.util.InfoRepositoryParsers
import com.nevmem.moneysaver.data.util.ParseError
import com.nevmem.moneysaver.data.util.ParseResult
import com.nevmem.moneysaver.data.util.ParsedValue
import com.nevmem.moneysaver.room.AppDatabase
import org.json.JSONObject
import java.lang.IllegalStateException
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository
@Inject constructor(
    var networkQueue: NetworkQueueBase, var appDatabase: AppDatabase,
    var executor: Executor, var userHolder: UserHolder
) {
    private var tag = "I_REP"
    var info = MutableLiveData<Info>(Info())
    var lastMonthDescription = MutableLiveData<MonthDescription>()
    var error = MutableLiveData<String>("")
    var loading = MutableLiveData<Boolean>(false)

    init {
        loadFromDatabase()
        loadFromNet()
    }

    fun tryUpdate() {
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
            with(appDatabase.monthDescriptionDao()) {
                val lastMonth = getLastMonth()
                if (lastMonth != null)
                    lastMonthDescription.postValue(lastMonth)
            }
        }
    }

    private fun resolveInfoConflicts(info: Info) {
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
            loadFromDatabase()
        }
    }

    private fun resolveMonthsDescriptionsConflicts(monthsDescriptions: ArrayList<MonthDescription>) {
        executor.execute {
            with(appDatabase.monthDescriptionDao()) {
                val usedIds = HashSet<String>()
                for (index in 0 until (monthsDescriptions.size)) {
                    val replica = getByMonthID(monthsDescriptions[index].monthId)
                    usedIds.add(monthsDescriptions[index].monthId)
                    if (replica == null) {
                        insert(monthsDescriptions[index])
                        i(tag, "Inserted")
                    } else {
                        monthsDescriptions[index].uid = replica.uid
                        update(monthsDescriptions[index])
                        i(tag, "Updated")
                    }
                }
                getAll().forEach {
                    if (!usedIds.contains(it.monthId)) {
                        delete(it)
                    }
                }
            }
            loadFromDatabase()
        }
    }

    private fun loadFromNet() {
        val params = userHolder.credentialsJson()
        params.put("info7", true)
        params.put("info30", true)
        params.put("daysDescription", true)
        params.put("months", true)
        loading.postValue(true)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiInfo, params, {
            loading.postValue(false)
            when (val parseResult = InfoRepositoryParsers.parseServerLoadedResponse(it)) {
                is ParseError -> { error.postValue(parseResult.reason) }
                is ParsedValue<*> -> {
                    val parsed = parseResult.parsed
                    if (parsed is InfoRepositoryParsers.Companion.InfoMonthDescriptionsPair) {
                        resolveInfoConflicts(parsed.info)
                        resolveMonthsDescriptionsConflicts(parsed.monthDescription)
                        loadFromDatabase()
                    } else {
                        throw IllegalStateException("Returned not a info month description pair from parse method")
                    }
                }
            }
        })
    }
}