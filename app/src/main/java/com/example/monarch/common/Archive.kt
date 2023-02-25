package com.example.monarch.common

import android.app.AppOpsManager
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity


// -----------------------------------------
// Получение информации о времени использования устройства за сутки минимум
// -----------------------------------------

//private fun checkUsageStatsPermission(): Boolean {
//    val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
//    // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
//    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        appOpsManager.unsafeCheckOpNoThrow(
//            "android:get_usage_stats",
//            Process.myUid(),
//            packageName
//        )
//    } else {
//        appOpsManager.checkOpNoThrow(
//            "android:get_usage_stats",
//            Process.myUid(),
//            packageName
//        )
//    }
//    return mode == AppOpsManager.MODE_ALLOWED
//}

//@SuppressLint("ServiceCast")
//    fun getStateUsage() {
//        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        var list: MutableList<UsageStats>
//        var list2: Map<String, UsageStats>
//
//        val cal = Calendar.getInstance()
//        cal.add(Calendar.YEAR, -1)
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//            // первый способ
////            val userManager = getSystemService(Context.USER_SERVICE) as UserManager
////            if (userManager.isUserUnlocked) {
////                list = statsManager.queryUsageStats(
////                    UsageStatsManager.INTERVAL_YEARLY,
////                    cal.timeInMillis,
////                    System.currentTimeMillis()
////                )
////                for (i in 0 until list.size) {
//                    // Все приложения в фоне
////                    Log.i(TAG, "${list[i].packageName}, ${getDate(list[i].firstTimeStamp)}, ${getDate(list[i].lastTimeStamp)}")
//                    // Всего времени в приложениях (работает по другому)
////                    Log.i(TAG, "${list[i].packageName}, ${getDate(list[i].totalTimeInForeground)}")
//
//                     // Всего времени в приложениях (работает по другому)
////                    if (list[i].lastTimeUsed > 0) {
////                        Log.i(
////                            TAG,
////                            "${list[i].packageName}, ${getDate(list[i].totalTimeInForeground)}, ${getDate(list[i].lastTimeUsed)}"
////                        )
////                    }
////                }
//
//                // второй способ
//                list2 = statsManager.queryAndAggregateUsageStats(
//                    cal.timeInMillis,
//                    System.currentTimeMillis()
//                )
//
//
//                for ((key, value) in list2) {
//                    if (value!!.packageName == "com.google.android.networkstack.tethering") {
////                        Log.i(
////                            TAG,
////                            "${value.packageName}, ${getDate(value.firstTimeStamp)}, ${getDate(value.lastTimeStamp)}"
////                        )
////                        Log.i(TAG, "$key\n")
//                    }
//                }
//
////                Log.i(TAG, "readContacts: $list2")
//            }
//        }
//    }









//    private fun addInPackageAndSort1(packageName: String, timeInForeground: Long) {
//        // если не первый
//        var findElemPosition = -1
//        for (currentPosition in 0 until timeUsedInfo.size) {
//            if (timeInForeground > timeUsedInfo[currentPosition].getTimeInForeground()) {
//                timeUsedInfo.add(
//                    TimeUsed(
//                        packageName = packageName,
//                        position = currentPosition + 1,
//                        timeInForeground = timeInForeground
//                    )
//                )
//                findElemPosition = currentPosition
//            }
//        }
//
//        if (findElemPosition > -1) {
//            for (currentPosition in findElemPosition until timeUsedInfo.size) {
//
//            }
//        }
//
//        if (findElemPosition) {
//            timeUsedInfo[currentPosition].setPosition(currentPosition + 1)
//        }
//
//        // eсли элемент первый
//        if (!findElemPosition) {
//            timeUsedInfo.add(
//                TimeUsed(
//                    packageName = packageName,
//                    position = 0,
//                    timeInForeground = timeInForeground
//                )
//            )
//
//            for (currentPosition in 0 until timeUsedInfo.size) {
//                timeUsedInfo[currentPosition].setPosition(currentPosition + 1)
//            }
//        }
//    }