package com.nevmem.moneysaver.dagger.components

import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.fragments.TemplatesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(mainPage: MainPage)

    fun inject(templatesFragment: TemplatesFragment)
}