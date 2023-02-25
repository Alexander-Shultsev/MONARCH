package com.example.monarch.common

import java.text.SimpleDateFormat
import java.util.*

class DateTime {
    companion object {
        fun getDate(milliSeconds: Long): String? {
            // Create a DateFormatter object for displaying date in specified format.
            val dateFormat = "dd/MM/yyyy HH:mm:ss.SSS"
            val formatter = SimpleDateFormat(dateFormat, Locale.forLanguageTag("RU"))

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun getTime(milliSeconds: Long): String {
            val secondTotal = milliSeconds / 1000
            val minuteTotal = secondTotal / 60
            val hourTotal = minuteTotal / 60
            val day = hourTotal / 24
            val hour = hourTotal - day
            val _minuteInHour = hour * 60
            val minute = minuteTotal - _minuteInHour
            val second = secondTotal - _minuteInHour * 60 - minute * 60

            if (day == 0L && hour == 0L && minute == 0L && second == 0L) {
                return "0"
            }
            if (day == 0L && hour == 0L && minute == 0L) {
                return "${second}c"
            }
            if (day == 0L && hour == 0L) {
                return "${minute}м ${second}c"
            }
            if (day == 0L) {
                return "${hour}ч ${minute}м ${second}c"
            }

            return "${day}д ${hour}ч ${minute}м ${second}c"
        }
    }
}