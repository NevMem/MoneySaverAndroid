package com.nevmem.moneysaver.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.data.repositories.WalletsRepository
import com.nevmem.moneysaver.data.util.*
import com.nevmem.moneysaver.room.entity.Tag
import com.nevmem.moneysaver.room.entity.Wallet
import javax.inject.Inject

class AddFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var addingState = MutableLiveData<RequestState>(NoneState)
    private var app: App = application as App

    @Inject
    lateinit var historyRepo: HistoryRepository

    @Inject
    lateinit var tagsRepo: TagsRepository

    @Inject
    lateinit var walletsRepo: WalletsRepository

    init {
        app.appComponent.inject(this)
        walletsRepo.tryUpdate()
        tagsRepo.tryUpdate()
    }

    val tags: MutableLiveData<List<Tag>>
        get() = tagsRepo.tags

    val wallets: MutableLiveData<List<Wallet>>
        get() = walletsRepo.wallets

    val tagAddingState: MutableLiveData<RequestState>
        get() = tagsRepo.addingState

    val walletAddingState: MutableLiveData<RequestState>
        get() = walletsRepo.addingState

    fun receivedTagAddingError() {
        tagsRepo.receivedAddingError()
    }

    fun receivedWalletAddingError() {
        walletsRepo.receivedAddingError()
    }

    fun addTag(tag: String) {
        tagsRepo.addTag(tag)
    }

    fun addWallet(wallet: String) {
        walletsRepo.addWallet(wallet)
    }

    fun add(record: Record) {
        addingState.postValue(LoadingState)
        historyRepo.addRecord(record) {
            if (it == null) {
                addingState.postValue(SuccessState("Successfully added record"))
            } else {
                addingState.postValue(ErrorState(it))
            }
        }
    }

    fun receivedInfo() {
        addingState.postValue(NoneState)
    }
}