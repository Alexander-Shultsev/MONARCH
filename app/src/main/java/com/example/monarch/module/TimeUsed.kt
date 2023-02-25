package com.example.monarch.module

class TimeUsed(
    private var packageName: String,
    private var position: Int,
    private var timeInForeground: Long
) {

    fun getPackageName(): String = packageName
    fun getTimeInForeground(): Long = timeInForeground
    fun getPosition(): Int = position

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun setTimeInForeground(timeInForeground: Long) {
        this.timeInForeground = timeInForeground
    }

    fun setPosition(position: Int) {
        this.position = position
    }
}