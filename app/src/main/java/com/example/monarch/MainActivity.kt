package com.example.monarch

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.monarch.ui.screen.MainScreen
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.viewmodel.TimeUsedViewModel


class MainActivity : ComponentActivity() {

    private lateinit var statsManager: UsageStatsManager
    private lateinit var appOpsManager: AppOpsManager
    private lateinit var viewModel: TimeUsedViewModel
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        createObserve()
        setContent()

//        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun init() {
        statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        viewModel = TimeUsedViewModel(statsManager)
        activity = this
    }

    private fun createObserve() {
        viewModel.action.observe(this) { newAction ->
            handleAction(newAction, viewModel)
        }
    }

    private fun setContent() {
        setContent {
            MonarchTheme {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // https://www.youtube.com/watch?v=xcfEQO0k_gU
                    // https://proandroiddev.com/accessing-app-usage-history-in-android-79c3af861ccf
                    // https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
                    MainScreen(
                        statsManager,
                        appOpsManager,
                        packageName,
                        viewModel,
                    )
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                viewModel.setGrantedUsageStatsPermission()
                Log.i(TAG, "registerLauncher: ${viewModel.stateUsagePermission}")
            }
        }

    private fun handleAction(
        action: TimeUsedViewModel.Action,
        viewModel: TimeUsedViewModel
    ) {
        when (action.getValue()) {
            TimeUsedViewModel.Action.QUERY_PERMISSION_STATE_USED -> {
//                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                resultLauncher.launch(intent)
            }
        }


    }
}
