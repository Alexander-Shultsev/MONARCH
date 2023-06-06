package com.example.monarch.module.common

import com.example.monarch.module.timeused.data.ConstantTimeUsage
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.dateDataBase
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.dateTimeFullFormat
import com.example.monarch.module.timeused.data.DateString
import java.util.*
import kotlin.collections.ArrayList

class DateTime {
    companion object {
        // секунды в 00д 00ч 00м 00с
        fun timeFormatter(seconds: Long): String {
            val minuteInSeconds = seconds / 60
            val hourInSeconds = minuteInSeconds / 60

            val day = hourInSeconds / 24
            val hour = hourInSeconds - day * 24
            val minute = minuteInSeconds - hourInSeconds * 60
            val second = seconds - minuteInSeconds * 60

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

        // массив миллисекунд в массив 2021-12-31 10:10:10
        fun timeFormatterInsert(milliSeconds: ArrayList<Long>): ArrayList<String> {
            val calendar = Calendar.getInstance()
            val dateTimeStringArray = arrayListOf<String>()

            for (elem in milliSeconds) {
                calendar.timeInMillis = elem
                dateTimeStringArray.add(dateTimeFullFormat.format(calendar.time))
            }
            return dateTimeStringArray
        }

        // разделить дату в виде в совокупности отдельных строк
        fun getDateString(date: Date): DateString {
            val day = ConstantTimeUsage.dayFormat.format(date)
            val month = ConstantTimeUsage.monthFullFormat.format(date)
            val dayOfWeek = ConstantTimeUsage.dayOfWeekFullFormat.format(date)
            val year = ConstantTimeUsage.yearFullFormat.format(date)

            return DateString(day, month, dayOfWeek, year)
        }

        // разделить дату в виде в совокупность отдельных строк
        fun getDateDataBase(date: Date): String {
            return dateDataBase.format(date)
        }
    }
}