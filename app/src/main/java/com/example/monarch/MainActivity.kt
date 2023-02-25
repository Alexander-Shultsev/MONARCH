package com.example.monarch

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
import androidx.appcompat.app.ActionBarDrawerToggle.Delegate
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import com.example.monarch.ui.screen.MainScreen
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.viewmodel.TimeUsedViewModel
import java.sql.Time
import java.util.*


class MainActivity : ComponentActivity() {

    private lateinit var statsManager: UsageStatsManager
    private lateinit var appOpsManager: AppOpsManager

    private var timeUsedViewModel: TimeUsedViewModel =
        TimeUsedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeUsedViewModel.action.observe(this) {
            Log.i(TAG, "1")
            handleAction(it)
        }
        statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

        setContent {
            MonarchTheme {
                // https://proandroiddev.com/accessing-app-usage-history-in-android-79c3af861ccf
                // https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
                MainScreen(
                    statsManager,
                    appOpsManager,
                    packageName
                )
            }
        }
    }

    private fun handleAction(action: TimeUsedViewModel.Action) {
        Log.i(TAG, "${action.getValue()}")
        when (action.getValue()) {
            TimeUsedViewModel.Action.QUERY_PERMISSION_STATE_USED -> {
                Log.i(TAG, "onCreate: 3")
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
    }
}
