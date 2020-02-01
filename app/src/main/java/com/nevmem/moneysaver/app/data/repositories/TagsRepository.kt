package com.nevmem.moneysaver.app.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.*
import com.nevmem.moneysaver.app.data.util.*
import com.nevmem.moneysaver.app.room.AppDatabase
import com.nevmem.moneysaver.app.utils.DataUtils
import com.nevmem.moneysaver.common.data.ByHourStatistics
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.common.data.RecordDate
import com.nevmem.moneysaver.common.data.Tag
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Singleton
class TagsRepository @Inject constructor(
    val networkQueue: com.nevmem.moneysaver.network.NetworkQueue,
    val appDatabase: AppDatabase,
    val executor: Executor,
    val userHolder: com.nevmem.moneysaver.auth.UserHolder,
    historyRepository: HistoryRepository,
    private val settingsManager: SettingsManager
) {
    private var logTag = "TAGS_REPOSITORY"

    val loading = MutableLiveData<Boolean>(false)
    val error = MutableLiveData<String>("")

    private val tagsList = BehaviorSubject.create<List<Tag>>()

    var addingState = MutableLiveData<RequestState>(NoneState)

    val tags: Observable<List<Tag>> = Observable
        .combineLatest(
            tagsList,
            historyRepository.historyObservable(),
            BiFunction<List<Tag>, List<Record>, Pair<List<Tag>, ByHourStatistics>>
            { list, history -> Pair(list, DataUtils.collectByHourStatistics(history)) })
        .map {
            applyTransform(it.first, it.second)
        }

    init {
        i(logTag, "init")
        loadFromDatabase()
        tryUpdate()
    }

    fun tryUpdate() {
        loadFromNet()
    }

    fun addTag(tag: String) {
        addingState.postValue(LoadingState)
        val params = userHolder.credentialsJson()
        params.put("tagName", tag)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiAddTag, params)
        request.success {
            addingState.postValue(NoneState)
            when (val parsed = TagsRepositoryParsers.parseAddTagResponse(it)) {
                is ParseError -> {
                    addingState.postValue(ErrorState(parsed.reason))
                }
                is ParsedValue<*> -> {
                    val data = parsed.parsed as String
                    addingState.postValue(SuccessState(data))
                }
            }
            tryUpdate()
        }
    }

    private fun loadFromNet() {
        error.postValue("")
        loading.postValue(true)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiTags, userHolder.credentialsJson(), {
            if (it.has("type")) {
                when {
                    it.getString("type") == "ok" -> {
                        val loaded = ArrayList<Tag>()
                        val fromJson = it.getJSONArray("data")
                        for (i in 0 until (fromJson.length()))
                            loaded.add(Tag(fromJson[i].toString()))
                        resolveConflicts(loaded)
                    }
                    it.getString("type") == "error" -> error.postValue(it.getString("error"))
                    else -> error.postValue("Unknown server response format")
                }
            }
            loading.postValue(false)
        })
    }

    private fun resolveConflicts(list: List<Tag>) {
        executor.execute {
            val names = HashSet<String>()
            list.forEach {
                names.add(it.name)
                if (appDatabase.tagsDao().findByName(it.name) == null) {
                    appDatabase.tagsDao().insert(it)
                    i(logTag, "Inserting")
                }
            }
            with (appDatabase.tagsDao()) {
                getAll().forEach {
                    if (!names.contains(it.name)) {
                        delete(it)
                    }
                }
            }
            loadFromDatabase()
        }
    }

    private fun loadFromDatabase() {
        executor.execute {
            with (appDatabase.tagsDao()) {
                tagsList.onNext(getAll())
            }
        }
    }

    private fun applyTransform(list: List<Tag>, stats: ByHourStatistics): List<Tag> {
        val currentHour = RecordDate.currentDate()
        val arrayList = ArrayList<Tag>()
        arrayList.addAll(list)
        if (settingsManager.isFeatureEnabled(Features.FEATURE_PREDICTIVE_TAGS)) {
            arrayList.sortWith(Comparator { first, second ->
                val hourStat = stats.hourStats(currentHour.hour)
                (hourStat[second.name] ?: 0) - (hourStat[first.name] ?: 0)
            })
        }
        return arrayList
    }

    fun getTagsAsList(): List<String> {
        return tags.blockingFirst().map { it.name }
    }

    fun receivedAddingError() {
        addingState.postValue(NoneState)
    }

    fun delete(tagName: String): MutableLiveData<RequestState> {
        val state = MutableLiveData<RequestState>(LoadingState)
        val params = userHolder.credentialsJson()
        params.put("tagName", tagName)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiRemoveTag, params)
        request.success {
            when (val parseResult = TagsRepositoryParsers.parseRemoveTagResponse(it)) {
                is ParseError -> {
                    state.postValue(ErrorState(parseResult.reason))
                }
                is ParsedValue<*> -> {
                    state.postValue(SuccessState())
                    loadFromNet()
                }
            }
        }
        return state
    }
}
