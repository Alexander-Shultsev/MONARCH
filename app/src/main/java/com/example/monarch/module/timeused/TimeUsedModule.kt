package com.example.monarch.module.timeused

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.text.format.Time
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.module.common.App.Companion.getContextInstanse
import com.example.monarch.module.common.DateTime
import com.example.monarch.module.timeused.data.Constant.Companion.CURRENT_DATE
import com.example.monarch.module.timeused.data.Constant.Companion.MINIMUM_GET_TIME
import com.example.monarch.module.timeused.data.Constant.Companion.dateFormat
import com.example.monarch.module.timeused.data.TimeUsed
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageInsert
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KMutableProperty0


class TimeUsedModule : ViewModel() {

    // статистика использования приложений
    private val statsManager: UsageStatsManager =
        getContextInstanse().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // проверка выдачи разрешения на получение статистики использования приложений
    private val appOpsManager: AppOpsManager =
        getContextInstanse().getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager

    // получения информации об установленных приложениях
    private val packageManager: PackageManager = getContextInstanse().packageManager

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

    // найден ли элемент в массиве
    private var findElem: Boolean = false

    init {
        val startDateTime = arrayListOf("2021-01-01 10:10:10", "2021-01-02 10:10:10")
        val endDateTime = arrayListOf("2021-01-01 10:12:10", "2021-01-02 10:13:10")
        val appLabel = arrayListOf("telegram", "telegram")
        val appNameId = arrayListOf("org.telegram", "org.telegram")
        val fkUser = arrayListOf(1, 1)

        timeUsageQuery.postTimeUsage(
            startDateTime,
            endDateTime,
            appLabel,
            appNameId,
            fkUser
        )

        _currentDate.value = CURRENT_DATE
        _dateDialogIsVisible.value = false
        _stateUsagePermission.value = checkUsageStatsPermission()
        _timeUsedInfo.value = getStateUsageFromEvent(CURRENT_DATE, statsManager)

        getPackageLabels()
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

    // получить статистику за день
    fun getStateUsageFromEvent(
        startTime: Date,
        statsManager: UsageStatsManager
    ): ArrayList<TimeUsed> {
        // обнуление данных
        val eventList = HashMap<String, MutableList<UsageEvents.Event>>()
        val endTime = Calendar.getInstance()
        var currentEvent: UsageEvents.Event
        val timeUsedInfoBuffer = ArrayList<TimeUsed>()
//        val timeUsageInsert = TimeUsageInsert()

        endTime.time = startTime
        endTime.add(Calendar.DAY_OF_MONTH, 1)

        // получение событий статистики использования
        val statsEvent = statsManager.queryEvents(
            startTime.time,
            endTime.timeInMillis
        )

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
            }
        }

        // перебор всех совокупностей событий в пакетах
        for (elem in eventList) {
            val elemEventsCount = elem.value.size
            val packageName = elem.key
            var timeInPackage: Long = 0 // время использования устройства в миллисекундах

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
                addInPackageAndSort(
                    packageName,
                    timeInPackage,
                    timeUsedInfoBuffer,
                    packages
                )
            }
        }

        return timeUsedInfoBuffer
    }

    // добавление информации о времени использования приложения в массив
    private fun addInPackageAndSort(
        packageName: String, timeInForeground: Long,
        timeUsedInfoBuffer: ArrayList<TimeUsed>,
        packages: List<ApplicationInfo>
    ) {
        timeUsedInfoBuffer.add( // создание пустого элемента
            TimeUsed(
                packageName = "",
                timeInForeground = 0L,
                applicationName = ""
            )
        )

        for (packageInfo in packages) { // нахождение имени приложения, соответствующего имени пакета
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

                        timeUsedInfoBuffer[changeIndex] = TimeUsed(
                            packageName = timeUsedInfoBuffer[changeIndexBefore].getPackageName(),
                            timeInForeground = timeUsedInfoBuffer[changeIndexBefore].getTimeInForeground(),
                            applicationName = timeUsedInfoBuffer[changeIndexBefore].getApplicationName(),
                        )

                        changeIndex--
                    }

                    timeUsedInfoBuffer[currentPosition] = TimeUsed(
                        packageName = packageName,
                        timeInForeground = timeInForeground,
                        applicationName = applicationName,
                    )

                    findElem = true
                    return
                }
            }

            if (!findElem) { // если элемент не найден, добавить его в конец массива
                val position = timeUsedInfoBuffer.size - 1

                timeUsedInfoBuffer[position] = TimeUsed(
                    packageName = packageName,
                    timeInForeground = timeInForeground,
                    applicationName = applicationName
                )
            }
        } else {
            timeUsedInfoBuffer[0] = TimeUsed(
                packageName = packageName,
                timeInForeground = timeInForeground,
                applicationName = applicationName,
            )
        }
        return
    }

    // при изменении даты
    fun onDateSelected(date: Date) {
        _timeUsedInfo.value = getStateUsageFromEvent(date, statsManager)
        _currentDate.value = date
    }

    // изменение видимости диалогового окна
    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }
}