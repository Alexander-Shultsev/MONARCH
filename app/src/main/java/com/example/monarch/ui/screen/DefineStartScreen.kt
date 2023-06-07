package com.example.monarch.ui.screen


import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import com.example.monarch.ui.component.NavHostMain
import com.example.monarch.ui.component.NavMainItem
import com.example.monarch.viewModel.common.SharedPreference
import com.example.monarch.viewModel.common.SharedPreferences
import com.example.monarch.viewModel.main.PermissionViewModel
import com.example.monarch.viewModel.timeused.TimeUsedViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@Composable
fun DefineStartScreen(
    permissionViewModel: PermissionViewModel = getViewModel()
) {
    val stateUsagePermission = permissionViewModel.stateUsagePermission.observeAsState(false)

    if (stateUsagePermission.value) {
        NavHostMain(NavMainItem.NavigationScreen.route)
    } else {
        RequestPermissionGetStateUsageScreen()
    }
}