package com.example.monarch.di

import com.example.monarch.viewModel.common.SharedPreference
import com.example.monarch.viewModel.timeused.TimeUsedViewModel
import com.example.monarch.viewModel.main.PermissionViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::TimeUsedViewModel)
    viewModelOf(::PermissionViewModel)

    singleOf(::SharedPreference)
}