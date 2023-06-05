package com.example.monarch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.monarch.repository.TimeUsage.TimeUsageQuery
import com.example.monarch.ui.screen.MainScreen
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.module.timeused.TimeUsedModule
import com.example.monarch.module.timeused.jobservice.JobServiceMain


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: TimeUsedModule

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        init()
        createObserve()
        setContent()
        service()
    }

    private fun init() {
        viewModel = TimeUsedModule()
    }

    private fun setContent() {
        setContent {
            MonarchTheme {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }

    private fun service() {
        val service = JobServiceMain()
        service.startJob()
    }

    private fun createObserve() {
        viewModel.action.observe(this) { newAction ->
            handleAction(newAction)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                viewModel.setGrantedUsageStatsPermission()
            }
        }

    private fun handleAction(
        action: TimeUsedModule.Action
    ) {
        when (action.getValue()) {
            TimeUsedModule.Action.QUERY_PERMISSION_STATE_USED -> {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                resultLauncher.launch(intent)
            }
        }
    }
}
