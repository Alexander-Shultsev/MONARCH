package com.example.monarch.model.TimeUsage.TimeUsageData

data class TimeUsageInsert (
    val startDateTime: ArrayList<Long> = arrayListOf(),
    val endDateTime: ArrayList<Long> = arrayListOf(),
    val appLabel: ArrayList<String> = arrayListOf(),
    val appNameId: ArrayList<String> = arrayListOf(),
    val fkUser: ArrayList<Int> = arrayListOf(),
)
