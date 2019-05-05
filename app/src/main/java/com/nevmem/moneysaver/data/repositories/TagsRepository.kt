package com.nevmem.moneysaver.data.repositories

import android.util.Log.i
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.NetworkQueueBase
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.util.*
import com.nevmem.moneysaver.room.AppDatabase
import com.nevmem.moneysaver.room.entity.Tag
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsRepository @Inject constructor(
    var networkQueue: NetworkQueueBase,
    var appDatabase: AppDatabase,
    var executor: Executor,
    var userHolder: UserHolder
) {
    var loading = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<String>("")
    var tags = MutableLiveData<List<Tag>>(ArrayList())

    var addingState = MutableLiveData<RequestState>(NoneState)

    private var tag = "T_REP"

    init {
        i(tag, "init")
        tryUpdate()
    }

    fun tryUpdate() {
        loadFromDatabase()
        loadFromNet()
    }

    fun addTag(tag: String) {
        addingState.postValue(LoadingState)
        val params = userHolder.credentialsJson()
        params.put("tagName", tag)
        val request = networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiAddTag, params)
        request.success {
            addingState.postValue(NoneState)
            val parsed = TagsRepositoryParsers.parseAddTagResponse(it)
            when (parsed) {
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
                    i(tag, "Inserting")
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
                tags.postValue(getAll())
            }
        }
    }

    fun getTagsAsList(): List<String> {
        val result = ArrayList<String>()
        tags.value?.forEach {
            result.add(it.name)
        }
        return result
    }
}