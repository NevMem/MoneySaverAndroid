package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.i
import com.nevmem.moneysaver.app.dagger.components.AppComponent
import com.nevmem.moneysaver.app.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.app.dagger.modules.DataModule
import com.nevmem.moneysaver.app.dagger.modules.NetworkModule
import com.nevmem.moneysaver.app.data.Info

class App : Application() {
    var info: Info

    var appComponent: AppComponent

    private val tag = "App"

    init {
        i(tag, "Components initialization")
        info = Info()

        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule(this))
            .networkModule(NetworkModule(this))
            .build()
    }
}
