package com.example.monarch.view.screen


import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.monarch.view.navigation.NavHostMain
import com.example.monarch.view.navigation.NavMainItem
import com.example.monarch.view.screen.RequestPermissionGetStateUsageScreen.RequestPermissionGetStateUsageScreen
import com.example.monarch.viewModel.permission.PermissionViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun DefineStartScreen(
    permissionViewModel: PermissionViewModel = getViewModel()
) {
    val stateUsagePermission = permissionViewModel.stateUsagePermission.observeAsState(false)

    if (stateUsagePermission.value) {
        NavHostMain(NavMainItem.AddExperiment.route)
    } else {
        RequestPermissionGetStateUsageScreen()
    }
}