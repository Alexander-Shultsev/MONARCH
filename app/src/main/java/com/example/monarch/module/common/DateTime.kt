package com.example.monarch.module.common

import com.example.monarch.module.timeused.data.Constant
import com.example.monarch.module.timeused.data.DateString
import java.util.*

class DateTime {
    companion object {
        // миллисекунды в 00д 00ч 00м 00с
        fun timeFormatter(milliSeconds: Long): String { 
            val secondInMillisecond = milliSeconds / 1000
            val minuteInMillisecond = secondInMillisecond / 60
            val hourInMillisecond = minuteInMillisecond / 60
            val day = hourInMillisecond / 24
            val hour = hourInMillisecond - day
            val minuteInHour = hour * 60
            val minute = minuteInMillisecond - minuteInHour
            val second = secondInMillisecond - minuteInHour * 60 - minute * 60

            if (day == 0L && hour == 0L && minute == 0L && second == 0L) {
                return "нет данных"
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

        // разделить дату в виде в совокупность отдельных строк
        fun getDateString(date: Date): DateString {
            val day = Constant.dayFormat.format(date)
            val month = Constant.monthFullFormat.format(date)
            val dayOfWeek = Constant.dayOfWeekFullFormat.format(date)
            val year = Constant.yearFullFormat.format(date)

            return DateString(day, month, dayOfWeek, year)
        }
    }
}