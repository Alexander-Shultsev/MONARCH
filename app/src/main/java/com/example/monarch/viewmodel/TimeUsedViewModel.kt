package com.example.monarch.viewmodel

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.module.TimeUsed
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.HashMap

class TimeUsedViewModel(val statsManager: UsageStatsManager): ViewModel() {

    companion object {
        const val MINIMUM_GET_TIME: Long = 1000L // минимально собираемый промежуток времени одной сессии
        var DEFAULT_DATE: Date = Date() // минимально собираемый промежуток времени одной сессии
        init {
            val formatter = SimpleDateFormat("MM-dd-yyyy", Locale("RU"))
            DEFAULT_DATE = formatter.parse(formatter.format(Date())) as Date




        }
    }

    val dayFormat = SimpleDateFormat("d", Locale("RU"))
    val monthFormat = SimpleDateFormat("MMMM", Locale("RU"))
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("RU"))
    val yearFormat = SimpleDateFormat("yyyy", Locale("RU"))

    private var eventList = HashMap<String, MutableList<UsageEvents.Event>>()
    private var timeInPackage: Long = 0L

    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible

    private val _stateUsagePermission: MutableLiveData<Boolean> = MutableLiveData()
    val stateUsagePermission: LiveData<Boolean> = _stateUsagePermission

    private val _currentDate: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    val currentDate: LiveData<HashMap<String, String>> = _currentDate

    private val _timeUsedInfo: MutableLiveData<ArrayList<TimeUsed>> = MutableLiveData()
    val timeUsedInfo: LiveData<ArrayList<TimeUsed>> = _timeUsedInfo
    var timeUsedInfoBuffer = ArrayList<TimeUsed>() // временная переменная для динамического хранения списка

    init {
        _timeUsedInfo.value = arrayListOf()
        _dateDialogIsVisible.value = false

        getDateSeparate(DEFAULT_DATE)
    }

    private fun getDateSeparate(date: Date) {
        val day = dayFormat.format(date)
        val month = monthFormat.format(date)
        val dayOfWeek = dayOfWeekFormat.format(date)
        val year = yearFormat.format(date)

        _currentDate.value?.set("day", day)
        _currentDate.value?.set("month", month)
        _currentDate.value?.set("dayOfWeek", dayOfWeek)
        _currentDate.value?.set("year", year)
    }

    class Action(private var value: Int) {
        companion object {
            const val QUERY_PERMISSION_STATE_USED = 0
            const val TOAST = 1
        }

        fun getValue(): Int {
            return value
        }

        fun setValue(value: Int) {
            this.value = value
        }
    }

    fun getStateUsageFromEvent(
        statsManager: UsageStatsManager,
        date: Date
    ) {
        eventList = HashMap()
        timeUsedInfoBuffer = arrayListOf()
        var currentEvent: UsageEvents.Event

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR, 23)
        calendar.add(Calendar.MINUTE, 59)
        calendar.add(Calendar.SECOND, 59)

//        val parser = SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale("RU"))
//        val startTime = parser.parse("${date}T00:00:00")?.time ?: 0
//        val endTime = parser.parse("${date}T23:59:59")?.time ?: 0

        val statsEvent = statsManager.queryEvents(
            date.time,
            calendar.timeInMillis
        )

        Log.i(TAG, "getStateUsageFromEvent: ${date.time}, ${calendar.timeInMillis}")

        // TODO объединить 2 цикла в 1

        while (statsEvent.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            statsEvent.getNextEvent(currentEvent)
            if (currentEvent.eventType == 1 ||
                currentEvent.eventType == 2
            ) {
                val key = currentEvent.packageName
                if (eventList[key] == null) { // check package is not in map
                    eventList[key] = mutableListOf()
                }
                eventList[key]!!.add(currentEvent)
            }
        }


        // перебор всех пакетов с группой событий
        for (elem in eventList) {
            val elemEventsCount = elem.value.size
            if (elemEventsCount > 1) {
                val packageName = elem.key
                timeInPackage = 0

                // перебор всех событий в пакете
                for (event in 0 until elemEventsCount - 1) {
                    val event0 = elem.value[event]
                    val event1 = elem.value[event + 1]

                    if (event0.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                        && event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                    ) {
                        val timeInForeground: Long = event1.timeStamp - event0.timeStamp
                        timeInPackage += timeInForeground
                    }
                }
                if (timeInPackage >= MINIMUM_GET_TIME) {
                    addInPackageAndSort(packageName, timeInPackage)
                }
            }
        }

        _timeUsedInfo.value = timeUsedInfoBuffer
    }


    // --------------------------------------------------
    // Выдача разрешение на сбор данных об использовании устройства
    // --------------------------------------------------

    fun setGrantedUsageStatsPermission() {
        _stateUsagePermission.value = true
    }

    fun isUsageStatsPermission(
        statsManager: UsageStatsManager,
        appOpsManager: AppOpsManager,
        packageName: String
    ) {
        _stateUsagePermission.value = checkUsageStatsPermission(appOpsManager, packageName)
        if (_stateUsagePermission.value!!) {
            getStateUsageFromEvent(statsManager, DEFAULT_DATE)
        } else {
            _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)
        }
    }

    fun checkUsageStatsPermission(
        appOpsManager: AppOpsManager,
        packageName: String
    ): Boolean {
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android-sdk => 29
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                packageName
            )
        } else {
            appOpsManager.checkOpNoThrow( // android-sdk < 29
                "android:get_usage_stats",
                Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun addInPackageAndSort(packageName: String, timeInForeground: Long): Boolean {
        timeUsedInfoBuffer.add(
            TimeUsed(
                packageName = "",
                position = 0,
                timeInForeground = 0L
            )
        )

        if (timeUsedInfoBuffer.size > 1) {
            var findElem = false
            for (currentPosition in 0 until timeUsedInfoBuffer.size - 1) {
                if (timeInForeground > timeUsedInfoBuffer[currentPosition].getTimeInForeground()) {
                    var changeIndex = timeUsedInfoBuffer.size - 1

                    while (changeIndex > currentPosition) {
                        timeUsedInfoBuffer[changeIndex].apply {
                            this.setPackageName(timeUsedInfoBuffer[changeIndex - 1].getPackageName())
                            this.setTimeInForeground(timeUsedInfoBuffer[changeIndex - 1].getTimeInForeground())
                        }
                        changeIndex--
                    }

                    timeUsedInfoBuffer[currentPosition].setPackageName(packageName)
                    timeUsedInfoBuffer[currentPosition].setTimeInForeground(timeInForeground)
                    findElem = true
                    return true
                }
            }

            if (!findElem) {
                timeUsedInfoBuffer[timeUsedInfoBuffer.size - 1].apply {
                    this.setPackageName(packageName)
                    this.setTimeInForeground(timeInForeground)
                }
            }
        } else {
            timeUsedInfoBuffer[0].apply {
                this.setPackageName(packageName)
                this.setTimeInForeground(timeInForeground)
            }
        }
        return true
    }

    fun onDateSelected(date: Date) {
        getStateUsageFromEvent(statsManager, date)
        getDateSeparate(date)
    }

    fun closeDialog() {
        _dateDialogIsVisible.value = false
    }

    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }
}