package com.nevmem.moneysaver

import android.app.Application
import android.util.Log.i
import com.nevmem.moneysaver.dagger.components.AppComponent
import com.nevmem.moneysaver.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.data.Info

class App : Application() {
    var info: Info

    var appComponent: AppComponent

    init {
        i("APP_CLASS", "App() init method was called")
        info = Info()

        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule(this))
            .networkModule(NetworkModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        i("APP_CLASS", "onCreate method was called")
    }
}
