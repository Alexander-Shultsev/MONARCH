package com.example.monarch.module

class TimeUsed(
    private var packageName: String,
    private var timeInForeground: Long,
    private var applicationName: String
) {

    fun getPackageName(): String = packageName
    fun getTimeInForeground(): Long = timeInForeground
    fun getApplicationName(): String = applicationName

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun setTimeInForeground(timeInForeground: Long) {
        this.timeInForeground = timeInForeground
    }

    fun setApplicationName(applicationName: String) {
        this.applicationName = applicationName
    }
}