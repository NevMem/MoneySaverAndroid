package com.nevmem.moneysaver.dagger.components

import com.nevmem.moneysaver.FullDescriptionActivity
import com.nevmem.moneysaver.LoginPageActivity
import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.fragments.AddFragment
import com.nevmem.moneysaver.fragments.DashboardFragment
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.TemplatesFragment
import com.nevmem.moneysaver.views.NewTemplateDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(mainPage: MainPage)
    fun inject(templatesFragment: TemplatesFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(addFragment: AddFragment)
    fun inject(activity: FullDescriptionActivity)
    fun inject(newTemplateDialog: NewTemplateDialog)
    fun inject(dashboardFragment: DashboardFragment)
    fun inject(loginPageActivity: LoginPageActivity)
}