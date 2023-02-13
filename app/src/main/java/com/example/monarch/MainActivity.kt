package com.example.monarch

import android.Manifest.permission.PACKAGE_USAGE_STATS
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
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
import androidx.core.content.ContextCompat
import com.example.monarch.ui.theme.MonarchTheme
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonarchTheme {
                // https://proandroiddev.com/accessing-app-usage-history-in-android-79c3af861ccf
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))

                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            PACKAGE_USAGE_STATS
                        ) == PackageManager.PERMISSION_GRANTED)
                    {
                        readContacts()
                    }
                    else
                    {
                        Log.i(TAG, "onCreate: noPermission")
                    }
                }
            }
        }
    }

    private fun checkUsageStatsPermission() : Boolean {
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

//    fun tryGetUsageState() {

//        val beginCal: Calendar = Calendar.getInstance()
//        beginCal.set(Calendar.DATE, 1)
//        beginCal.set(Calendar.MONTH, 0)
//        beginCal.set(Calendar.YEAR, 2012)
//
//        val endCal: Calendar = Calendar.getInstance()
//        endCal.set(Calendar.DATE, 1)
//        endCal.set(Calendar.MONTH, 0)
//        endCal.set(Calendar.YEAR, 2014)

//        val requestPermissionLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ) { isGranted: Boolean ->
//                if (isGranted) {
//                    Log.i(TAG, "true")
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                } else {
//                    Log.i(TAG, "false")
//                    // Explain to the user that the feature is unavailable because the
//                    // feature requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                }
//            }


//        when {
//            ContextCompat.checkSelfPermission(applicationContext, PACKAGE_USAGE_STATS)
//                    == PackageManager.PERMISSION_GRANTED
//            -> {
//                readContacts()
//            }
//            ContextCompat.checkSelfPermission(applicationContext, CAMERA)
//                    == PackageManager.PERMISSION_GRANTED
//            -> {
//                readContacts()
//            }
//
////            ActivityCompat.shouldShowRequestPermissionRationale(
////                this,
////                PACKAGE_USAGE_STATS
////            ) -> {
////                requestPermissionLauncher.launch(PACKAGE_USAGE_STATS)
////            }
//            else -> {
//                requestPermissionLauncher.launch(CAMERA)
//            }
//        }
//    }

//    @Deprecated("Deprecated in Java")
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            0 -> {
//                Log.i(TAG, "0")
//            }
////            0 -> {
////                // If request is cancelled, the result arrays are empty.
////                if ((grantResults.isNotEmpty() &&
////                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
////                    // Permission is granted. Continue the action or workflow
////                    // in your app.
////                } else {
////                    // Explain to the user that the feature is unavailable because
////                    // the feature requires a permission that the user has denied.
////                    // At the same time, respect the user's decision. Don't link to
////                    // system settings in an effort to convince the user to change
////                    // their decision.
////                }
////                return
////            }
//            else -> {
//                Log.i(TAG, requestCode.toString())
//            }
//        }
//    }

    fun readContacts() {
        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val list : MutableList<UsageStats>

        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)

        list = statsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            cal.getTimeInMillis(),
            System.currentTimeMillis()
        )

        Log.i(TAG, "tryGetUsageState: $list")
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