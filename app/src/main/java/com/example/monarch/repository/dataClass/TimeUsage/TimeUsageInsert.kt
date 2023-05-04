package com.example.monarch.repository.dataClass.TimeUsage

import com.example.monarch.module.common.DateTime
import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class TimeUsageInsert (
    val startDateTime: ArrayList<String> = arrayListOf(),
    val endDateTime: ArrayList<String> = arrayListOf(),
    val appLabel: ArrayList<String> = arrayListOf(),
    val appNameId: ArrayList<String> = arrayListOf(),
    val fkUser: ArrayList<Int> = arrayListOf(),
)
