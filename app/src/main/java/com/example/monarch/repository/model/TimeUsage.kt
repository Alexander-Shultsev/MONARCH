package com.example.monarch.repository.model

import com.example.monarch.module.common.DateTime
import java.util.Date

data class TimeUsage (
    val idTimeUsage: Int,
    val device: String,
    val startTime: DateTime,
    val endTime: DateTime,
    val duration: Int,
    val packageName: String,
    val userPackageName: String,
    val fkUser: Int,
)
