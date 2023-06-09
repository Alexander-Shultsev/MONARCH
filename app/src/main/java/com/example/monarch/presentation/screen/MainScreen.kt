package com.example.monarch.presentation.screen


import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.example.monarch.presentation.navigation.NavHostMain
import com.example.monarch.presentation.navigation.NavMainItem
import com.example.monarch.presentation.screen.RequestPermissionGetStateUsageScreen.RequestPermissionGetStateUsageScreen
import com.example.monarch.viewModel.main.PermissionViewModel
import org.koin.androidx.compose.getViewModel


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