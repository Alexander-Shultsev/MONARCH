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
import androidx.lifecycle.viewModelScope
import com.example.monarch.module.TimeUsed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TimeUsedViewModel: ViewModel() {

    companion object {
        const val MINIMUM_GET_TIME: Long = 1000L // минимально собираемый промежуток времени одной сессии
    }

    private val sameEvents = HashMap<String, MutableList<UsageEvents.Event>>()
    private var timeInPackage: Long = 0L

    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    private val _timeUsedInfo: MutableLiveData<ArrayList<TimeUsed>> = MutableLiveData(arrayListOf())
    val timeUsedInfo: LiveData<ArrayList<TimeUsed>> = _timeUsedInfo

    class Action(private var value: Int) {
        companion object {
            const val QUERY_PERMISSION_STATE_USED = 0
            const val OTHER = 1
        }

        fun getValue(): Int {
            return value
        }

        fun setValue(value: Int) {
            this.value = value
        }
    }

//    ---------------------------------------
    sealed class Event {
        object NavigateToSettings: Event()
        data class ShowSnackBar(val text: String): Event()
        data class ShowToast(val text: String): Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar("Sample"))
            eventChannel.send(Event.ShowToast("Toast"))
        }
    }

    fun settingsButtonClicked() {
        viewModelScope.launch {
            eventChannel.send(Event.NavigateToSettings)
        }
    }

    //    ---------------------------------------

    private fun getStateUsageFromEvent(statsManager: UsageStatsManager) {
        var currentEvent: UsageEvents.Event

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("RU"))
        val startTime = parser.parse("2023-02-24T10:00:00")?.time ?: 0
        val endTime = parser.parse("2023-02-25T10:00:00")?.time ?: 0

        val statsEvent = statsManager.queryEvents(
            startTime,
            endTime
        )

        // TODO объединить 2 цикла в 1

        while (statsEvent.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            statsEvent.getNextEvent(currentEvent)
            if (currentEvent.eventType == 1 ||
                currentEvent.eventType == 2
            ) {
                val key = currentEvent.packageName
                if (sameEvents[key] == null) { // check package is not in map
                    sameEvents[key] = mutableListOf()
                }
                sameEvents[key]!!.add(currentEvent)
            }
        }

        // перебор всех пакетов с группой событий
        for (elem in sameEvents) {
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
    }

    fun isUsageStatsPermission(
        statsManager: UsageStatsManager,
        appOpsManager: AppOpsManager,
        packageName: String
    ) {
        Log.i(TAG, "isUsageStatsPermission: ${action.value}")
        Log.i(TAG, "isUsageStatsPermission: ${_action.value}")

        _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)


        Log.i(TAG, "isUsageStatsPermission: ${action.value}")
        Log.i(TAG, "isUsageStatsPermission: ${_action.value}")
//        if (checkUsageStatsPermission(appOpsManager, packageName)) {
//            getStateUsageFromEvent(statsManager)
//        } else {
//            action.value?.setValue(Action.QUERY_PERMISSION_STATE_USED)
//        }
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
        timeUsedInfo.value?.add(
            TimeUsed(
                packageName = "",
                position = 0,
                timeInForeground = 0L
            )
        )

        if (timeUsedInfo.value?.size!! > 1) {
            var findElem = false
            for (currentPosition in 0 until timeUsedInfo.value!!.size - 1) {
                if (timeInForeground > timeUsedInfo.value!![currentPosition].getTimeInForeground()) {
                    var changeIndex = timeUsedInfo.value!!.size - 1

                    while (changeIndex > currentPosition) {
                        timeUsedInfo.value!![changeIndex].apply {
                            this.setPackageName(timeUsedInfo.value!![changeIndex - 1].getPackageName())
                            this.setTimeInForeground(timeUsedInfo.value!![changeIndex - 1].getTimeInForeground())
                        }
                        changeIndex--
                    }

                    timeUsedInfo.value!![currentPosition].setPackageName(packageName)
                    timeUsedInfo.value!![currentPosition].setTimeInForeground(timeInForeground)
                    findElem = true
                    return true
                }
            }

            if (!findElem) {
                timeUsedInfo.value!![timeUsedInfo.value!!.size - 1].apply {
                    this.setPackageName(packageName)
                    this.setTimeInForeground(timeInForeground)
                }
            }
        } else {
            timeUsedInfo.value!![0].apply {
                this.setPackageName(packageName)
                this.setTimeInForeground(timeInForeground)
            }
        }
        return true
    }
}