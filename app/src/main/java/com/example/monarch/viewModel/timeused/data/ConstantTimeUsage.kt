package com.example.monarch.viewModel.timeused.data

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ConstantTimeUsage {
    companion object {
        const val MINIMUM_GET_TIME: Long = 1000L // минимально собираемый промежуток времени одной сессии
        var TODAY_DATE: Date = Date()

        // промежуток времени за который собирается время использования
        const val TIME_FOR_COLLECT = 1
        val UNIT_OF_MEASUREMENT_FOR_JOB_SCHEDULER = TimeUnit.HOURS
        const val UNIT_OF_MEASUREMENT_FOR_FUNCTION = Calendar.HOUR

        private val currentDate = SimpleDateFormat("MM-dd-yyyy", Locale("RU"))
        val dateDataBase = SimpleDateFormat("yyyy-MM-dd", Locale("RU"))
        val dayFormat = SimpleDateFormat("d", Locale("RU"))
        val monthFullFormat = SimpleDateFormat("MMMM", Locale("RU"))
        val dayOfWeekFullFormat = SimpleDateFormat("EEE", Locale("RU"))
        val yearFullFormat = SimpleDateFormat("yyyy", Locale("RU"))
        val dateTimeFullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("RU"))
        val timeHourFormat = SimpleDateFormat("HH", Locale("RU"))
        val timeMinuteFormat = SimpleDateFormat("mm", Locale("RU"))
        val timeSecondFormat = SimpleDateFormat("ss", Locale("RU"))
        val dateOutput = SimpleDateFormat("dd.MM.yyyy", Locale("RU"))

        init {
            TODAY_DATE = currentDate.parse(currentDate.format(Date())) as Date
        }
    }
}