package com.example.monarch.di

import com.example.monarch.viewModel.common.SharedPreference
import com.example.monarch.view.screen.TimeUsedScreen.ExperimentInfoModel
import com.example.monarch.view.screen.ExperimentsScreen.ExperimentsViewModel
import com.example.monarch.view.screen.TimeUsedScreen.TimeUsedViewModel
import com.example.monarch.view.screen.AddExperiment.AddExperimentViewModel
import com.example.monarch.viewModel.permission.PermissionViewModel
import com.example.monarch.view.component.calendar.CalendarViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PermissionViewModel)
    viewModelOf(::ExperimentInfoModel)
    viewModelOf(::ExperimentsViewModel)
    viewModelOf(::TimeUsedViewModel)
    viewModelOf(::AddExperimentViewModel)
    viewModelOf(::CalendarViewModel)

    singleOf(::SharedPreference)
}