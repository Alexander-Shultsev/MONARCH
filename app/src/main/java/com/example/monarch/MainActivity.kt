package com.example.monarch

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStatsManager.*
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.monarch.ui.theme.MonarchTheme
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonarchTheme {
                // https://proandroiddev.com/accessing-app-usage-history-in-android-79c3af861ccf
                https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
//                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))

                    if (checkUsageStatsPermission()) {
                        getStateUsageFromEvent()
                    } else {
                        Log.i(TAG, "onCreate: noPermission")
                    }
                }
            }
        }
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getStateUsageFromEvent() {
        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("RU"))
        val startTime = parser.parse("2023-02-15T16:00:00")?.time ?: 0
        val endTime = parser.parse("2023-02-15T17:00:00")?.time ?: 0

        val statsEvent = statsManager.queryEvents(
            startTime,
            endTime
        )

        Log.i(TAG, statsEvent.toString())
    }

    @SuppressLint("ServiceCast")
    fun getStateUsage() {
        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var list: MutableList<UsageStats>
        var list2: Map<String, UsageStats>

        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // первый способ
            val userManager = getSystemService(Context.USER_SERVICE) as UserManager
            if (userManager.isUserUnlocked) {
                list = statsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_YEARLY,
                    cal.timeInMillis,
                    System.currentTimeMillis()
                )

                for (i in 0 until list.size) {
                    // Все приложения в фоне
//                    Log.i(TAG, "${list[i].packageName}, ${getDate(list[i].firstTimeStamp)}, ${getDate(list[i].lastTimeStamp)}")
                    // Всего времени в приложениях (работает по другому)
//                    Log.i(TAG, "${list[i].packageName}, ${getDate(list[i].totalTimeInForeground)}")

                     // Всего времени в приложениях (работает по другому)
                    if (list[i].lastTimeUsed > 0) {
//                        Log.i(
//                            TAG,
//                            "${list[i].packageName}, ${getDate(list[i].totalTimeInForeground)}, ${getDate(list[i].lastTimeUsed)}"
//                        )
                    }
                }

                // второй способ
                /* TODO проверить работу */
                list2 = statsManager.queryAndAggregateUsageStats(
                    cal.timeInMillis,
                    System.currentTimeMillis()
                )


                for ((key, value) in list2) {
                    if (value!!.packageName == "com.google.android.networkstack.tethering") {
//                        Log.i(
//                            TAG,
//                            "${value.packageName}, ${getDate(value.firstTimeStamp)}, ${getDate(value.lastTimeStamp)}"
//                        )
                        Log.i(TAG, "$key\n")
                    }
                }

//                Log.i(TAG, "readContacts: $list2")
            }
        }
    }

    fun getDate(milliSeconds: Long): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val dateFormat = "dd/MM/yyyy HH:mm:ss.SSS"
        val formatter = SimpleDateFormat(dateFormat, Locale.forLanguageTag("RU"))

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MonarchTheme {
        Greeting("Android")
    }
}