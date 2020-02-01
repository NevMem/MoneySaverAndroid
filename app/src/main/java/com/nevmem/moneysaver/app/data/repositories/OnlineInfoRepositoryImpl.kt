package com.nevmem.moneysaver.app.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.app.data.util.*
import com.nevmem.moneysaver.app.room.AppDatabase
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineInfoRepositoryImpl
@Inject constructor(
    var networkQueue: com.nevmem.moneysaver.network.NetworkQueue, var appDatabase: AppDatabase,
    var executor: Executor, var userHolder: com.nevmem.moneysaver.auth.UserHolder
) : InfoRepository {
    private var tag = "ONLINE_INFO_REPOSITORY"
    private var info = MutableLiveData<Info>(Info())
    private var requestState = MutableLiveData<RequestState>(NoneState)
    private var monthDescriptions = MutableLiveData<List<MonthDescription>>()

    init {
        loadFromDatabase()
        tryUpdate()
    }

    override fun tryUpdate() {
        loadFromNet()
    }

    override fun state() = requestState

    override fun info() = info

    override fun monthDescriptions() = monthDescriptions

    private fun loadFromDatabase() {
        executor.execute {
            with (appDatabase.infoDao()) {
                val saved = get()
                if (saved != null) {
                    info.postValue(saved)
                }
            }
            with (appDatabase.monthDescriptionDao()) {
                monthDescriptions.postValue(getAll())
            }
        }
    }

    private fun resolveInfoConflicts(info: Info) {
        executor.execute {
            with (appDatabase.infoDao()) {
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
        requestState.postValue(LoadingState)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiInfo, params, {
            requestState.postValue(NoneState)
            when (val parseResult = InfoRepositoryParsers.parseServerLoadedResponse(it)) {
                is ParseError -> {
                    requestState.postValue(ErrorState(parseResult.reason))
                }
                is ParsedValue<*> -> {
                    requestState.postValue(SuccessState())
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