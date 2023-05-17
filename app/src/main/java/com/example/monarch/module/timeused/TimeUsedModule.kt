package com.example.monarch.module.timeused

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.module.common.App.Companion.getContextInstance
import com.example.monarch.module.common.DateTime.Companion.timeFormatterInsert
import com.example.monarch.module.timeused.data.Constant.Companion.TODAY_DATE
import com.example.monarch.module.timeused.data.Constant.Companion.MINIMUM_GET_TIME
import com.example.monarch.module.timeused.data.TimeUsed
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageInsert
import java.util.Calendar
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TimeUsedModule : ViewModel() {

    // https://habr.com/ru/companies/epam_systems/articles/415335/
    // https://stackoverflow.com/questions/9177212/creating-background-service-in-android

    // статистика использования приложений
    private val statsManager: UsageStatsManager =
        getContextInstance().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // проверка выдачи разрешения на получение статистики использования приложений
    private val appOpsManager: AppOpsManager =
        getContextInstance().getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    // получения информации об установленных приложениях
    private val packageManager: PackageManager = getContextInstance().packageManager

    // вызываемое событие (переход на экран запроса разрешения)
    private val _action: MutableLiveData<Action> = MutableLiveData()
    val action: LiveData<Action> = _action

    // отображение диалогового окна
    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible

    // выдача разрешения на сбор статистики
    private val _stateUsagePermission: MutableLiveData<Boolean> = MutableLiveData()
    val stateUsagePermission: LiveData<Boolean> = _stateUsagePermission

    // выбранная дата
    private val _currentDate: MutableLiveData<Date> = MutableLiveData()
    val currentDate: LiveData<Date> = _currentDate

    // список TimeUsed
    private val _timeUsedInfo: MutableLiveData<ArrayList<TimeUsed>> = MutableLiveData(arrayListOf())
    val timeUsedInfo: LiveData<ArrayList<TimeUsed>> = _timeUsedInfo

    // запросы для модуля TimeUsage
    private val timeUsageQuery = TimeUsageQuery()

    // список установленных покетов
    private var packages: List<ApplicationInfo> = listOf()

    // имя приложения
    private var applicationName: String = ""

    // данные для запроса на добавление TimeUsage на сервер
    private var timeUsageInsert = TimeUsageInsert()

    // события от приложения
    class Action(private var value: Int) {
        companion object {
            const val QUERY_PERMISSION_STATE_USED = 0
        }

        fun getValue(): Int = value

        fun setValue(value: Int) {
            this.value = value
        }
    }

    // инициализация
    init {
        getPackageLabels()
//        requestSchedule()

        _currentDate.value = TODAY_DATE
        _dateDialogIsVisible.value = false
        _stateUsagePermission.value = checkUsageStatsPermission()
    }

    // получить названия всех установленных приложений на устройстве
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

    // выдача разрешение на сбор данных об использовании устройства
    fun setGrantedUsageStatsPermission() {
        _stateUsagePermission.value = true
    }

    // запуск запроса на разрешение при его отсутствии
    fun isUsageStatsPermission() {
        _stateUsagePermission.value = checkUsageStatsPermission()

        if (!_stateUsagePermission.value!!) { // если нет разрешений, дать их
            _action.value = Action(Action.QUERY_PERMISSION_STATE_USED)
        }
    }

    // проверка наличия разрешения
    private fun checkUsageStatsPermission(): Boolean {
        val context = getContextInstance()

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

    // получить статистику за день
    fun getStateUsageFromEvent(
        dateToday: Date
    ): TimeUsageInsert {
        // обнуление данных
        val eventList = HashMap<String, MutableList<UsageEvents.Event>>()
        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance()
        var currentEvent: UsageEvents.Event

        // временные данные для отправки на сервер
        val timeUsageInsertBuffer = TimeUsageInsert()

        startTime.time = dateToday
        endTime.time = dateToday

        startTime.add(Calendar.DAY_OF_MONTH, -1)
//        startTime.add(Calendar.DAY_OF_MONTH, -1)
//        startTime.add(Calendar.HOUR, -3)

        // получение событий статистики использования
        val statsEvent = statsManager.queryEvents(
            startTime.timeInMillis,
            endTime.timeInMillis
        )

        Log.i(TAG, startTime.timeInMillis.toString())
        Log.i(TAG, endTime.timeInMillis.toString())

        // TODO объединить 2 цикла в 1

        // распределение событий по пакетам
        while (statsEvent.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            statsEvent.getNextEvent(currentEvent)

            Log.i(TAG, "1")

            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
            ) {
                Log.i(TAG, "2")
                val key = currentEvent.packageName
                if (eventList[key] == null) { // если пакет не существует
                    eventList[key] = mutableListOf() // создать пустой пакет
                }
                eventList[key]!!.add(currentEvent)
            }
        }

        // перебор всех совокупностей событий в пакетах
        for (elem in eventList) {
            val elemEventsCount = elem.value.size
            val packageName = elem.key
            var timeInPackage: Long = 0 // время использования устройства в миллисекундах
            var endDataTime: Long = 0 // конец добавляемого используемого приложение
            var startDataTime: Long = 0 // начало добавляемого используемого приложение

            // перебор всех событий в пакете
            for (event in 0 until elemEventsCount - 1) {
                val event0 = elem.value[event]
                val event1 = elem.value[event + 1]

                if (event0.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                    && event1.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                ) {
                    endDataTime = event1.timeStamp
                    startDataTime = event0.timeStamp
                    val timeInForeground: Long = event1.timeStamp - event0.timeStamp
                    timeInPackage += timeInForeground
                }
            }
            if (timeInPackage >= MINIMUM_GET_TIME) {
                addInTimeUsageInsert(
                    packageName,
                    endDataTime,
                    startDataTime,
                    timeUsageInsertBuffer,
                    packages
                )
            }
        }

        // выполнение запроса на добавление TimeUsage
        timeUsageInsertBuffer.apply {
            timeUsageQuery.postTimeUsage(
                timeFormatterInsert(startDateTime),
                timeFormatterInsert(endDateTime),
                appLabel,
                appNameId,
                fkUser
            )
        }

        return timeUsageInsertBuffer
    }

    // добавление информации о времени использования приложения в массив
    private fun addInTimeUsageInsert(
        packageName: String,
        endTime: Long,
        startTime: Long,
        timeUsageInsertBuffer: TimeUsageInsert,
        packages: List<ApplicationInfo>
    ) {
        for (packageInfo in packages) { // нахождение имени приложения, соответствующего имени пакета
            val packageNameApplicationInfo = packageInfo.packageName

            if (packageNameApplicationInfo == packageName) {
                applicationName = packageInfo.loadLabel(packageManager).toString()
            }
        }

        // добавление элемента в массив для отправки на сервер
        timeUsageInsertBuffer.apply {
            appLabel.add(applicationName)
            appNameId.add(packageName)
            startDateTime.add(startTime)
            endDateTime.add(endTime)
            fkUser.add(1) // TODO временно по умолчанию
        }
    }

    // при изменении даты
    fun onDateSelected(date: Date) {
//        _timeUsedInfo.value = getStateUsageFromEvent(date, statsManager)
        _currentDate.value = date
    }

    // изменение видимости диалогового окна
    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }
}