package com.nevmem.moneysaver.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.NetworkQueue
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.room.AppDatabase
import org.json.JSONObject
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository
@Inject constructor(
    var networkQueue: NetworkQueue, var appDatabase: AppDatabase,
    var executor: Executor, var userHolder: UserHolder
) {
    private var tag = "I_REP"
    var info = MutableLiveData<Info>(Info())
    var lastMonthDescription = MutableLiveData<MonthDescription>()
    var error = MutableLiveData<String>("")
    var success = MutableLiveData<String>("")
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

    private fun parseMonthsDescriptions(json: JSONObject): ArrayList<MonthDescription> {
        val parsed = ArrayList<MonthDescription>()
        for (key in json.keys()) {
            val monthJson = json.getJSONObject(key)
            val monthDescription = MonthDescription()
            monthDescription.monthId = key
            monthDescription.total = monthJson.getDouble("total")
            monthDescription.average = monthJson.getDouble("average")
            monthDescription.totalDaily = monthJson.getDouble("totalDaily")
            monthDescription.averageDaily = monthJson.getDouble("averageDaily")
            monthDescription.monthTimestamp = monthJson.getInt("monthTimestamp")

            val byTag = monthJson.getJSONObject("byTag")
            for (tagName in byTag.keys()) {
                monthDescription.byTagDaily[tagName] = byTag.getJSONObject(tagName).getDouble("daily")
                monthDescription.byTagTotal[tagName] = byTag.getJSONObject(tagName).getDouble("total")
            }
            parsed.add(monthDescription)
        }
        return parsed
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
            if (it.has("type")) {
                val type = it.getString("type")
                if (type == "ok") {
                    val info = Info()
                    val infoJson = it.getJSONObject("info")
                    info.fromJSON(infoJson)

                    val monthSum = infoJson.getJSONObject("monthSum")
                    val monthsDescriptions = parseMonthsDescriptions(monthSum)

                    resolveInfoConflicts(info)
                    resolveMonthsDescriptionsConflicts(monthsDescriptions)
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