package com.nevmem.moneysaver.app.data.repositories

import androidx.lifecycle.LiveData
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.app.data.util.RequestState
import io.reactivex.rxjava3.core.Observable

interface HistoryRepository {
    fun tryUpdate()

    fun editingState(): LiveData<RequestState>

    fun history(): LiveData<ArrayList<Record>>
    fun error(): LiveData<String>
    fun loading(): LiveData<Boolean>

    fun historyObservable(): Observable<ArrayList<Record>>

    fun delete(record: Record, deleteCallback: (() -> Unit)? = null)
    fun addRecord(record: Record, cd : (String?) -> Unit)
    fun stopEditing()
    fun editRecord(record: Record)
}