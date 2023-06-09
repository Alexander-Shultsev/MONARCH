package com.example.monarch.di

import com.example.monarch.viewModel.common.SharedPreference
import com.example.monarch.presentation.screen.TimeUsedScreen.ExperimentInfoModel
import com.example.monarch.presentation.screen.ExperimentsScreen.ExperimentsViewModel
import com.example.monarch.presentation.screen.TimeUsedScreen.TimeUsedViewModel
import com.example.monarch.viewModel.main.PermissionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PermissionViewModel)
    viewModelOf(::ExperimentInfoModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::TimeUsedViewModel)

    singleOf(::SharedPreference)
}