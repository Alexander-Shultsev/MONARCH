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
import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.monarch.common.DatePicker
import com.example.monarch.ui.screen.MainScreen
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.viewmodel.TimeUsedViewModel
import java.text.SimpleDateFormat
import java.util.*


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
    }

    private fun init() {
        statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        viewModel = TimeUsedViewModel(statsManager)
        activity = this
    }

    private fun createObserve() {
        viewModel.action.observe(this) { newAction ->
            handleAction(newAction)
        }
    }

    private fun setContent() {
        setContent {
            MonarchTheme {
                // https://www.youtube.com/watch?v=xcfEQO0k_gU
                // https://proandroiddev.com/accessing-app-usage-history-in-android-79c3af861ccf
                // https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
                MainScreen(
                    statsManager,
                    appOpsManager,
                    packageName,
                    viewModel,
                    this
                )
            }
        }
    }

    private fun handleAction(action: TimeUsedViewModel.Action) {
        when (action.getValue()) {
            TimeUsedViewModel.Action.QUERY_PERMISSION_STATE_USED -> {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
    }
}
