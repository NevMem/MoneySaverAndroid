package com.nevmem.moneysaver.app.data.repositories

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log.i
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.Template
import com.nevmem.moneysaver.app.data.TemplateBase
import com.nevmem.moneysaver.app.room.AppDatabase
import com.nevmem.moneysaver.app.room.entity.StoredTemplate
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class TemplatesRepository @Inject constructor(
    private var networkQueue: com.nevmem.moneysaver.network.NetworkQueue,
    private var userHolder: com.nevmem.moneysaver.auth.UserHolder,
    private var appDatabase: AppDatabase,
    private var context: Context,
    private var executor: Executor
) {
    var templates: MutableLiveData<ArrayList<Template>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    init {
        i("TR", "constructor was called")
        this.executor.execute {
            val list = appDatabase.templateDao().loadAll()
            val buffer = ArrayList<Template>()
            i("EXECUTOR", "was loaded: ${list.size} items")
            list.forEach {
                buffer.add(Template(it))
            }
            Handler(Looper.getMainLooper()).post { templates.value = buffer }
        }
    }

    private fun updateOrInsert(template: Template) {
        executor.execute {
            try {
                val stored = appDatabase.templateDao().findByIdOne(template.id)
                if (stored != null) {
                    stored.updateBy(template)
                    appDatabase.templateDao().update(stored)
                    i("EXECUTOR", "Updated")
                } else {
                    appDatabase.templateDao().insert(StoredTemplate(template))
                    i("EXECUTOR", "Inserted")
                }
            } catch (e: Exception) {
                i("EXECUTOR", e.message)
            }
        }
    }

    private fun parseLoadedTemplates(array: JSONArray): ArrayList<Template> {
        val result = ArrayList<Template>()
        for (index in 0 until (array.length())) {
            val cur = array.getJSONObject(index)
            i("TR", cur.toString())
            try {
                val template = Template(
                    cur.getString("name"), cur.getDouble("value"),
                    cur.getString("tag"), cur.getString("wallet"),
                    cur.getString("id")
                )
                i("TR", template.tag)
                result.add(template)
            } catch (_: Exception) {
            }
        }
        return result
    }

    private fun updateError(templateIndex: Int, err: String) {
        if (templates.value != null) {
            val buffer = templates.value!!
            buffer[templateIndex].error = err
            buffer[templateIndex].sending = false
            templates.value = buffer
        }
    }

    private fun updateSuccess(templateIndex: Int, value: Boolean = true) {
        if (templates.value != null) {
            val buffer = templates.value!!
            buffer[templateIndex].error = null
            buffer[templateIndex].sending = false
            buffer[templateIndex].success = value
            templates.value = buffer

            if (value) {
                Handler().postDelayed({
                    updateSuccess(templateIndex, false)
                }, 1000)
            }
        }
    }

    private fun createDate(): JSONObject {
        val curCalendar = Calendar.getInstance()
        val date = JSONObject()
        date.put("year", curCalendar.get(Calendar.YEAR))
        date.put("month", curCalendar.get(Calendar.MONTH) + 1)
        date.put("day", curCalendar.get(Calendar.DATE))
        date.put("hour", curCalendar.get(Calendar.HOUR_OF_DAY))
        date.put("minute", curCalendar.get(Calendar.MINUTE))
        return date
    }

    fun createNewTemplate(base: TemplateBase) {
        val params = userHolder.credentialsJson()
        params.put("name", base.name)
        params.put("value", base.value)
        params.put("wallet", base.wallet)
        params.put("tag", base.tag)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiCreateTemplate, params, {
            if (it.has("type")) {
                when {
                    it.getString("type") == "ok" -> {
                        Toast.makeText(context, "Template was successfully added", Toast.LENGTH_LONG).show()
                        tryUpdate()
                    }
                    it.getString("type") == "error" -> Toast.makeText(context, it.getString("error"), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(context, "Server response has unknown format", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Server response has unknown format", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun resolveDataConflicts(loaded: ArrayList<Template>) {
        executor.execute {
            val saved = appDatabase.templateDao().loadAll()
            val loadedIds = HashSet<String>()
            loaded.forEach {
                loadedIds.add(it.id)
                updateOrInsert(it)
            }
            saved.forEach {
                if (!loadedIds.contains(it.id)) {
                    appDatabase.templateDao().delete(it)
                }
            }
            i("EXECUTOR", "All conflicts was successfully resolved")
        }
    }

    fun useTemplate(templateIndex: Int) {
        if (templates.value != null) {
            val buffer = templates.value!!
            buffer[templateIndex].error = ""
            buffer[templateIndex].success = false
            buffer[templateIndex].sending = true
            templates.value = buffer
        }
        val params = userHolder.credentialsJson()
        params.put("templateId", templates.value!![templateIndex].id)
        params.put("date", createDate())
        i("TR", params.toString())
        networkQueue.infinitePostJsonObjectRequest(
            Vars.ServerApiUseTemplate, params,
            {
                i("TR", it.toString())
                if (it.has("type")) {
                    when {
                        it.getString("type") == "ok" -> updateSuccess(templateIndex)
                        it.getString("type") == "error" -> updateError(templateIndex, it.getString("error"))
                        else -> updateError(templateIndex, "Server response has unknown format")
                    }
                } else {
                    updateError(templateIndex, "Server response has unknown format")
                }
            })
    }

    fun removeTemplate(id: String) {
        val params = userHolder.credentialsJson()
        params.put("templateId", id)
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiRemoveTemplate, params, {
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    tryUpdate()
                } else {
                }
            } else {
                // TODO: (Unresolved server response format)
            }
        })
    }

    private fun proceedResponse(str: String) {
        error.value = ""
        val json = JSONObject(str)
        if (json.has("type")) {
            when {
                json.getString("type") == "ok" -> {
                    val array = json.getJSONArray("data")
                    val parsed = parseLoadedTemplates(array)
                    templates.value = parsed
                    resolveDataConflicts(parsed)
                }
                json.getString("type") == "error" -> error.value = json.getString("error")
                else -> error.value = "Server response has unknown format"
            }
        } else {
            error.value = "Server response has unknown format"
        }
    }

    fun tryUpdate() {
        i("TR", "Trying to update")
        loading.value = true
        networkQueue.infinitePostStringRequest(Vars.ServerApiTemplates, userHolder.credentialsJson(), {
            loading.value = false
            proceedResponse(it)
        })
    }
}