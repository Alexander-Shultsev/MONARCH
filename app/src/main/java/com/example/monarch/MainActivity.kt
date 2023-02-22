package com.example.monarch

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
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
                // https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
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

        val currentEvent = UsageEvents.Event()
        val map = HashMap<String, AppUsageInfo>()
        val sameEvents = HashMap<String, ArrayList<UsageEvents.Event>>()

        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("RU"))
        val startTime = parser.parse("2023-02-15T16:00:00")?.time ?: 0
        val endTime = parser.parse("2024-02-15T17:00:00")?.time ?: 0

        val statsEvent = statsManager.queryEvents(
            startTime,
            endTime
        )

        while (statsEvent.hasNextEvent()) {
            statsEvent.getNextEvent(currentEvent)
            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                currentEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED
            ) {
                val key = currentEvent.packageName
                if (map[key] == null) { // check package is not in map
                    map.put(key, AppUsageInfo(key))
                    sameEvents.put(key, ArrayList<UsageEvents.Event>())
                }
                sameEvents[key]!!.add(currentEvent)
            }
        }

        for (elem in sameEvents) {
            val elemEventsCount = elem.value.size
            if (elemEventsCount > 1) {
                    for (event in 0 until elemEventsCount - 1) {
                        val event0 = elem.value[event]
                        val event1 = elem.value[event + 1]

                        if (event0.eventType == Event.ACTIVITY_RESUMED
                            || event1.eventType == Event.ACTIVITY_RESUMED
                        ) {
                            map[event1.packageName]!!.launchCount++;
                        }

                        Log.i(TAG, "${event0.eventType}")

//                        if (event0.eventType == Event.ACTIVITY_RESUMED
//                            && event1.eventType == Event.ACTIVITY_STOPPED
//                        ) {
//                            val timeInForeground: Long = event1.timeStamp - event0.timeStamp;
//
//                            Log.i(TAG, "${elem.key}")
//                            Log.i(TAG, "${getDate(elem.value[event].timeStamp)}")
//                            Log.i(TAG, "${getDate(elem.value[event + 1].timeStamp)}")
//                            Log.i(TAG, "${getDate(timeInForeground)}")
//
//                            map[event0.packageName]!!.timeInForeground += timeInForeground
//                        }
                }
            }
        }

        // Traverse through each app data which is grouped together and count launch, calculate duration
//        for (Map.Entry<String,List<UsageEvents.Event>> entry : sameEvents.entrySet()) {
//            int totalEvents = entry . getValue ().size();
//            if (totalEvents > 1) {
//                for (int i = 0; i < totalEvents - 1; i++) {
//                    UsageEvents.Event E0 = entry . getValue ().get(i);
//                    UsageEvents.Event E1 = entry . getValue ().get(i + 1);
//
//                    if (E1.getEventType() == 1 || E0.getEventType() == 1) {
//                        map.get(E1.getPackageName()).launchCount++;
//                    }
//
//                    if (E0.getEventType() == 1 && E1.getEventType() == 2) {
//                        long diff = E1 . getTimeStamp () - E0.getTimeStamp();
//                        map.get(E0.getPackageName()).timeInForeground += diff;
//                    }
//                }
//            }
//
//            // If First eventtype is ACTIVITY_PAUSED then added the difference of start_time and Event occuring time because the application is already running.
//            if (entry.getValue().get(0).getEventType() == 2) {
//                long diff = entry . getValue ().get(0).getTimeStamp() - start_time;
//                map.get(entry.getValue().get(0).getPackageName()).timeInForeground += diff;
//            }
//
//            // If Last eventtype is ACTIVITY_RESUMED then added the difference of end_time and Event occuring time because the application is still running .
//            if (entry.getValue().get(totalEvents - 1).getEventType() == 1) {
//                long diff = end_time -entry.getValue().get(totalEvents - 1).getTimeStamp();
//                map.get(
//                    entry.getValue().get(totalEvents - 1).getPackageName()
//                ).timeInForeground += diff;
//            }
//        }
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