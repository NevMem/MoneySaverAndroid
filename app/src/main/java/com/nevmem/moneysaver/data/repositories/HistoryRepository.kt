package com.nevmem.moneysaver.data.repositories

import androidx.lifecycle.LiveData
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.util.RequestState

interface HistoryRepository {
    fun tryUpdate()

    fun editingState(): LiveData<RequestState>

    fun history(): LiveData<ArrayList<Record>>
    fun error(): LiveData<String>
    fun loading(): LiveData<Boolean>

    fun delete(record: Record)
    fun addRecord(record: Record, cd : (String?) -> Unit)
    fun stopEditing()
    fun editRecord(record: Record)
}