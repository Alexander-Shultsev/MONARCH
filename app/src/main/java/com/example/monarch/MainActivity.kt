package com.example.monarch

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.viewModel.timeused.TimeUsedViewModel
import com.example.monarch.viewModel.timeused.jobservice.JobServiceMain
import com.example.monarch.ui.screen.DefineStartScreen
import com.example.monarch.viewModel.main.PermissionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val permissionViewModel by viewModel<PermissionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        createObserve()
        setContent()
        service()
    }

    private fun setContent() {
        setContent {
            MonarchTheme {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                ) {
                    DefineStartScreen()
                }
            }
        }
    }

    private fun service() {
        val service = JobServiceMain()
        service.startJob()
    }

    private fun createObserve() {
        permissionViewModel.action.observe(this) { newAction ->
            handleAction(newAction)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                permissionViewModel.setGrantedUsageStatsPermission()
            }
        }

    private fun handleAction(
        action: PermissionViewModel.Action
    ) {
        when (action.getValue()) {
            PermissionViewModel.Action.QUERY_PERMISSION_STATE_USED -> {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                resultLauncher.launch(intent)
            }
        }
    }
}
