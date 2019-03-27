package com.nevmem.moneysaver.data

import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log.i
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.room.AppDatabase
import com.nevmem.moneysaver.room.entity.DBTemplate
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplatesRepository {
    var templates: MutableLiveData<ArrayList<Template>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    private var networkQueue: NetworkQueue
    private var userHolder: UserHolder

    private var appDatabase: AppDatabase

    @Inject
    constructor(networkQueue: NetworkQueue, userHolder: UserHolder,
                appDatabase: AppDatabase) {
        i("TR", "constructor was called")
        this.networkQueue = networkQueue
        this.userHolder = userHolder
        this.appDatabase = appDatabase
    }

    private fun parseLoadedTemplates(array: JSONArray): ArrayList<Template> {
        val result = ArrayList<Template>()
        for (index in 0 until(array.length())) {
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
            } catch (_: Exception) {}
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

    private fun updateSuccess(templateIndex: Int) {
        if (templates.value != null) {
            val buffer = templates.value!!
            buffer[templateIndex].error = ""
            buffer[templateIndex].sending = false
            buffer[templateIndex].success = true
            templates.value = buffer
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

    fun useTemplate(templateIndex: Int) {
        if (templates.value != null) {
            val buffer = templates.value!!
            buffer[templateIndex].error = ""
            buffer[templateIndex].success = false
            buffer[templateIndex].sending = true
            templates.value = buffer
        }
        val params= userHolder.credentialsJson()
        params.put("templateId", templates.value!![templateIndex].id)
        params.put("date", createDate())
        i("TR", params.toString())
        networkQueue.infinitePostJsonObjectRequest(Vars.ServerApiUseTemplate, params,
        {
            i("TR", it.toString())
            if (it.has("type")) {
                if (it.getString("type") == "ok") {
                    updateSuccess(templateIndex)
                } else if (it.getString("type") == "error") {
                    updateError(templateIndex, it.getString("error"))
                } else {
                    updateError(templateIndex, "Server response has unknown format")
                }
            } else {
                updateError(templateIndex, "Server response has unknown format")
            }
        })
    }

    private fun proceedResponse(str: String) {
        error.value = ""
        val json = JSONObject(str)
        if (json.has("type")) {
            if (json.getString("type") == "ok") {
                val array = json.getJSONArray("data")
                templates.value = parseLoadedTemplates(array)
            } else if (json.getString("type") == "error") {
                error.value = json.getString("error")
            } else {
                error.value = "Server response has unknown format"
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