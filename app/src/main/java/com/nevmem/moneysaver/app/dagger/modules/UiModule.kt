package com.nevmem.moneysaver.app.dagger.modules

import com.nevmem.moneysaver.app.controllers.SnackbarUseCaseImpl
import com.nevmem.moneysaver.ui.useCases.SnackbarUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UiModule {
    @Singleton
    @Provides
    fun providesSnackbarUseCase(impl: SnackbarUseCaseImpl): SnackbarUseCase = impl
}