package com.nevmem.moneysaver.app.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.UserHolder
import com.nevmem.moneysaver.app.data.repositories.InfoRepository
import javax.inject.Inject

class DashboardFragmentViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var infoRepo: InfoRepository

    @Inject
    lateinit var userHolder: UserHolder

    init {
        application as App
        application.appComponent.inject(this)
    }

    fun info(): LiveData<Info> = infoRepo.info()

    fun userHolder() = userHolder.user

    fun update() {
        infoRepo.tryUpdate()
    }

    fun state() = infoRepo.state()
}