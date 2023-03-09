package com.example.monarch.module.timeused

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.module.common.App.Companion.getContextInstanse
import com.example.monarch.module.timeused.data.TimeUsed
import java.text.SimpleDateFormat
import java.util.*


class TimeUsedModule : ViewModel() {

    companion object {
        const val MINIMUM_GET_TIME: Long =
            1000L // минимально собираемый промежуток времени одной сессии
        var DEFAULT_DATE: Date = Date() // минимально собираемый промежуток времени одной сессии

        init {
            val formatter = SimpleDateFormat("MM-dd-yyyy", Locale("RU"))
            DEFAULT_DATE = formatter.parse(formatter.format(Date())) as Date
        }
    }

    private var statsManager: UsageStatsManager =
        getContextInstanse().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private var appOpsManager: AppOpsManager =
        getContextInstanse().getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    private var packageManager: PackageManager = getContextInstanse().packageManager

    private var eventList = HashMap<String, MutableList<UsageEvents.Event>>()
    private var timeInPackage: Long = 0L

    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible

    private val _stateUsagePermission: MutableLiveData<Boolean> = MutableLiveData()
    val stateUsagePermission: LiveData<Boolean> = _stateUsagePermission

    var currentDate: Date

    private val _currentDateString: MutableLiveData<HashMap<String, String>> = MutableLiveData(HashMap())
    val currentDateString: LiveData<HashMap<String, String>> = _currentDateString

    private val _animateItem: MutableLiveData<Boolean> = MutableLiveData()
    val animateItem: LiveData<Boolean> = _animateItem

    private val _timeUsedInfo: MutableLiveData<ArrayList<TimeUsed>> = MutableLiveData()
    val timeUsedInfo: LiveData<ArrayList<TimeUsed>> = _timeUsedInfo
    var timeUsedInfoBuffer =
        ArrayList<TimeUsed>() // временная переменная для динамического хранения информации о времени использования приложений

    private val dayFormat = SimpleDateFormat("d", Locale("RU"))
    private val monthFormat = SimpleDateFormat("MMMM", Locale("RU"))
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale("RU"))
    private val yearFormat = SimpleDateFormat("yyyy", Locale("RU"))

    private var packages: List<ApplicationInfo>

    init {
        _timeUsedInfo.value = arrayListOf()
        _dateDialogIsVisible.value = false
        _animateItem.value = true
        packages = listOf()
        currentDate = DEFAULT_DATE

        getDateSeparate(DEFAULT_DATE)
        getPackageLabels()
    }

    private fun getPackageLabels() {
        packages = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } else {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(
                    PackageManager.GET_META_DATA.toLong()
                )
            )
        }
    }

    private fun getDateSeparate(date: Date) {
        val day = dayFormat.format(date)
        val month = monthFormat.format(date)
        val dayOfWeek = dayOfWeekFormat.format(date)
        val year = yearFormat.format(date)

        _currentDateString.value?.put("day", day)
        _currentDateString.value?.put("month", month)
        _currentDateString.value?.put("dayOfWeek", dayOfWeek)
        _currentDateString.value?.put("year", year)
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

    fun isUsageStatsPermission() {
        _stateUsagePermission.value = checkUsageStatsPermission()

        if (!_stateUsagePermission.value!!) { // если нет разрешений, дать их
            _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)
        }
    }

    fun checkUsageStatsPermission(): Boolean {
        val context = getContextInstanse()

        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // android-sdk => 29
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow( // android-sdk < 29
                "android:get_usage_stats",
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private var applicationName: String = ""
    private var findElem: Boolean = false

    private fun addInPackageAndSort(packageName: String, timeInForeground: Long): Boolean {
        timeUsedInfoBuffer.add( // создание пустого элемента
            TimeUsed(
                packageName = "",
                timeInForeground = 0L,
                applicationName = ""
            )
        )

        for (packageInfo in packages) { // нахождение имени приложения, соответсвующего имени пакета
            val packageNameApplicationInfo = packageInfo.packageName

            if (packageNameApplicationInfo == packageName) {
                applicationName = packageInfo.loadLabel(packageManager).toString()
            }
        }

        // определение позиции нового элемента в массиве
        if (timeUsedInfoBuffer.size > 1) {
            findElem = false
            for (currentPosition in 0 until timeUsedInfoBuffer.size - 1) {
                if (timeInForeground > timeUsedInfoBuffer[currentPosition].getTimeInForeground()) { // если новое время больше перебираемого
                    var changeIndex = timeUsedInfoBuffer.size - 1

                    while (changeIndex > currentPosition) {
                        val changeIndexBefore = changeIndex - 1 // номер элемента, куда переноситься текущий
                        createTimeUsed(
                            packageName = timeUsedInfoBuffer[changeIndexBefore].getPackageName(),
                            timeInForeground = timeUsedInfoBuffer[changeIndexBefore].getTimeInForeground(),
                            applicationName = timeUsedInfoBuffer[changeIndexBefore].getApplicationName(),
                            position = changeIndex
                        )

                        changeIndex--
                    }

                    createTimeUsed(
                        packageName = packageName,
                        timeInForeground = timeInForeground,
                        applicationName = applicationName,
                        position = currentPosition
                    )
                    findElem = true
                    return true
                }
            }

            if (!findElem) { // если элемент не найден, добавить его в конец массива
                createTimeUsed(
                    packageName = packageName,
                    timeInForeground = timeInForeground,
                    applicationName = applicationName,
                    position = timeUsedInfoBuffer.size - 1
                )
            }
        } else {
            createTimeUsed(
                packageName = packageName,
                timeInForeground = timeInForeground,
                applicationName = applicationName,
                position = 0
            )
        }
        return true
    }

    private fun createTimeUsed(
        packageName: String,
        timeInForeground: Long,
        applicationName: String,
        position: Int
    ) {
        timeUsedInfoBuffer[position].setPackageName(packageName)
        timeUsedInfoBuffer[position].setTimeInForeground(timeInForeground)
        timeUsedInfoBuffer[position].setApplicationName(applicationName)
    }

    fun onDateSelected(date: Date) {
        getStateUsageFromEvent(date)
        getDateSeparate(date)
        currentDate = date
    }

    fun closeDialog() {
        _dateDialogIsVisible.value = false
        _animateItem.value = true
    }

    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
        _animateItem.value = false
    }
}