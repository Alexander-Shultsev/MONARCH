package com.example.monarch.module.timeused.data

import java.text.SimpleDateFormat
import java.util.*

class Constant {
    companion object {
        const val MINIMUM_GET_TIME: Long = 1000L // минимально собираемый промежуток времени одной сессии
        var CURRENT_DATE: Date = Date()

        private val currentDate = SimpleDateFormat("MM-dd-yyyy", Locale("RU"))
        val dayFormat = SimpleDateFormat("d", Locale("RU"))
        val monthFullFormat = SimpleDateFormat("MMMM", Locale("RU"))
        val dayOfWeekFullFormat = SimpleDateFormat("EEE", Locale("RU"))
        val yearFullFormat = SimpleDateFormat("yyyy", Locale("RU"))
        val dateFormat  = SimpleDateFormat("MM-dd-yyyy", Locale("RU"))

        init {
            CURRENT_DATE = currentDate.parse(currentDate.format(Date())) as Date
        }
    }
}