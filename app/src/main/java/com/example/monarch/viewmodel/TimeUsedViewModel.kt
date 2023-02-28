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
import java.util.*

class TimeUsedViewModel(val statsManager: UsageStatsManager): ViewModel() {

    companion object {
        const val MINIMUM_GET_TIME: Long = 1000L // минимально собираемый промежуток времени одной сессии
        const val DEFAULT_DATE: String = "26.02.2023" // минимально собираемый промежуток времени одной сессии
    }

    private var eventList = HashMap<String, MutableList<UsageEvents.Event>>()
    private var timeInPackage: Long = 0L

    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible

    private val _timeUsedInfo: MutableLiveData<ArrayList<TimeUsed>> = MutableLiveData()
    val timeUsedInfo: LiveData<ArrayList<TimeUsed>> = _timeUsedInfo
    var timeUsedInfoBuffer = ArrayList<TimeUsed>() // временная переменная для динамического хранения списка

    init {
        _timeUsedInfo.value = arrayListOf()
        _dateDialogIsVisible.value = false
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

    private fun getStateUsageFromEvent(
        statsManager: UsageStatsManager,
        date: Date
    ) {
        eventList = HashMap<String, MutableList<UsageEvents.Event>>()
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
        Log.i(TAG, _timeUsedInfo.value.toString())
    }

    fun isUsageStatsPermission(
        statsManager: UsageStatsManager,
        appOpsManager: AppOpsManager,
        packageName: String
    ) {
        if (checkUsageStatsPermission(appOpsManager, packageName)) {
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("RU"))
            val dateToDate = formatter.parse(DEFAULT_DATE)
            getStateUsageFromEvent(statsManager, dateToDate!!)
        } else {
            _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)
        }
    }

    private fun checkUsageStatsPermission(
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
        Log.i(TAG, "addInPackageAndSort: 1")
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
    }

    fun closeDialog() {
        _dateDialogIsVisible.value = false
    }

    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }
}