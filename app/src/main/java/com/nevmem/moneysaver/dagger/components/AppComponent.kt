package com.nevmem.moneysaver.dagger.components

import com.nevmem.moneysaver.activity.*
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.fragments.*
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
    fun inject(dashboardPageMonthDescriptionFragment: DashboardPageMonthDescriptionFragment)
    fun inject(monthDescription: MonthDescriptionActivity)
    fun inject(registerPageViewModel: RegisterPageViewModel)
    fun inject(registerActivity: RegisterActivity)
    fun inject(splashScreen: SplashScreen)
    fun inject(fullDescriptionActivityViewModel: FullDescriptionActivityViewModel)
    fun inject(addFragmentViewModel: AddFragmentViewModel)
}