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
import com.example.monarch.module.common.Constant
import com.example.monarch.module.common.DateTime
import com.example.monarch.module.common.DateTime.Companion.timeFormatter
import com.example.monarch.module.common.DateTime.Companion.timeFormatterInsert
import com.example.monarch.module.timeused.data.ConstantTimeUsage
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.TODAY_DATE
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.MINIMUM_GET_TIME
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.TIME_FOR_COLLECT
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.UNIT_OF_MEASUREMENT_FOR_FUNCTION
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.timeHourFormat
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.timeMinuteFormat
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.timeSecondFormat
import com.example.monarch.repository.TimeUsage.TimeUsageQuery
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageDevice
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

    // список строк о времени использования устройства с сервера сгруппированного по приложениям
    private val _timeUsageDevice: MutableLiveData<ArrayList<TimeUsageDevice>> = MutableLiveData()
    val timeUsageDevice: LiveData<ArrayList<TimeUsageDevice>> = _timeUsageDevice

    // запросы для модуля TimeUsage
    private val timeUsageQuery = TimeUsageQuery()

    // список установленных покетов
    private var packages: List<ApplicationInfo> = listOf()

    // имя приложения
    private var applicationName: String = ""

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
        _timeUsageDevice.value = arrayListOf(TimeUsageDevice("", 0L))

        getTimeUsageDevice(_currentDate.value!!)
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

        endTime.time = dateToday

        // получение информации о текущем времени
        val currentHour = timeHourFormat.format(Date())
        val currentMinute = timeMinuteFormat.format(Date())
        val currentSecond = timeSecondFormat.format(Date())

        endTime.add(Calendar.HOUR, currentHour.toInt())
        endTime.add(Calendar.MINUTE, currentMinute.toInt())
        endTime.add(Calendar.SECOND, currentSecond.toInt())

        startTime.time = endTime.time

        startTime.add(UNIT_OF_MEASUREMENT_FOR_FUNCTION, -1 * TIME_FOR_COLLECT)

        // получение событий статистики использования
        val statsEvent = statsManager.queryEvents(
            startTime.timeInMillis,
            endTime.timeInMillis
        )
        var count = 0

        // TODO объединить 2 цикла в 1
        // распределение событий по пакетам
        while (statsEvent.hasNextEvent()) {
            currentEvent = UsageEvents.Event()
            statsEvent.getNextEvent(currentEvent)

            if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
            ) {
                val key = currentEvent.packageName
                if (eventList[key] == null) { // если пакет не существует
                    eventList[key] = mutableListOf() // создать пустой пакет
                }
                eventList[key]!!.add(currentEvent)
//                Log.i(TAG, "getStateUsageFromEvent: ${currentEvent.packageName} - ${currentEvent.eventType} - ${currentEvent.timeStamp}")
                count++
            }
        }

        Log.i(TAG, "count: ${count / 2}")

        var innerCount = 0

        // перебор всех совокупностей событий в пакетах
        for (elem in eventList) {
            val elemEventsCount = elem.value.size
            val packageName = elem.key
            var timeInPackage: Long = 0 // время использования устройства в миллисекундах
            var endDataTime: Long = 0 // конец добавляемого используемого приложение
            var startDataTime: Long = 0 // начало добавляемого используемого приложение
            var listStart = 0 // первый элемент в пакете для начала перебора

            Log.i(TAG, "pakage name - $packageName")

            // если первый элемент списка имеет тип ACTIVITY_PAUSED, то начать перебор со второго элемента
            val eventStart = elem.value[0]
            if (eventStart.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                listStart++
            }

            // перебор всех событий в пакете
            for (event in listStart until elemEventsCount - 1 step 2) {
                innerCount++

                val event0 = elem.value[event]
                val event1 = elem.value[event + 1]

                Log.i(TAG, "getStateUsageFromEvent: ${event0.packageName} - ${event0.eventType} - ${event0.timeStamp}")
                Log.i(TAG, "getStateUsageFromEvent: ${event1.packageName} - ${event1.eventType} - ${event1.timeStamp}")

                startDataTime = event0.timeStamp
                endDataTime = event1.timeStamp

                val timeInForeground: Long = endDataTime - startDataTime
                timeInPackage += timeInForeground

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

        Log.i(TAG, "count: ${innerCount}")

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
        _currentDate.value = date
        getTimeUsageDevice(date)
    }

    // изменение видимости диалогового окна
    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }

    // получить данные с сервера о времени использования устройства агрегированные по приложениям
    private fun getTimeUsageDevice(date: Date) {
        val dateString = DateTime.getDateDataBase(date)

        timeUsageQuery.getTimeUsageDevice(
            dateString,
            _timeUsageDevice
        )
    }
}