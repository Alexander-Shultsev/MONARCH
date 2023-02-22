package com.example.monarch

import android.graphics.drawable.Drawable


internal class AppUsageInfo(
    packageNameValue: String
) {
    var packageName: String? = packageNameValue
    var appName: String? = null
    var timeInForeground: Long = 0
    var launchCount = 0
}