package com.example.monarch.repository.dataClass.TimeUsage

import com.example.monarch.module.common.DateTime
import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class TimeUsageInsert (
    val startDateTime: ArrayList<Long> = arrayListOf(),
    val endDateTime: ArrayList<Long> = arrayListOf(),
    val appLabel: ArrayList<String> = arrayListOf(),
    val appNameId: ArrayList<String> = arrayListOf(),
    val fkUser: ArrayList<Int> = arrayListOf(),
)
