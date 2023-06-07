package com.example.monarch.viewModel.timeused

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
import com.example.monarch.di.App.Companion.getContextInstance
import com.example.monarch.viewModel.common.DateTime
import com.example.monarch.viewModel.common.DateTime.Companion.timeFormatterInsert
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.TODAY_DATE
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.MINIMUM_GET_TIME
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.TIME_FOR_COLLECT
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.UNIT_OF_MEASUREMENT_FOR_FUNCTION
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.timeHourFormat
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.timeMinuteFormat
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.timeSecondFormat
import com.example.monarch.repository.TimeUsage.TimeUsageQuery
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageDevice
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageInsert
import java.util.Calendar
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TimeUsedViewModel : ViewModel() {

    // https://habr.com/ru/companies/epam_systems/articles/415335/
    // https://stackoverflow.com/questions/9177212/creating-background-service-in-android

    // статистика использования приложений
    private val statsManager: UsageStatsManager =
        getContextInstance().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // получения информации об установленных приложениях
    private val packageManager: PackageManager = getContextInstance().packageManager



    // отображение диалогового окна
    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible



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



    // инициализация
    init {
        getPackageLabels()
//        requestSchedule()

        _currentDate.value = TODAY_DATE
        _dateDialogIsVisible.value = false
        _timeUsageDevice.value = arrayListOf(TimeUsageDevice("", 0L))

        getTimeUsageDevice(_currentDate.value!!)
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
    fun getStateUsageFromEvent(dateToday: Date) {
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
            var endDataTime: Long // конец добавляемого используемого приложение
            var startDataTime: Long // начало добавляемого используемого приложение
            var timeInForeground: Long
            var listStart = 0 // первый элемент в пакете для начала перебора

            // если первый элемент списка имеет тип ACTIVITY_PAUSED, то начать перебор со второго элемента
            val eventStart = elem.value[0]
            if (eventStart.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                listStart++
            }

            // перебор всех событий в пакете
            for (event in listStart until elemEventsCount - 1 step 2) {
                val event0 = elem.value[event]
                val event1 = elem.value[event + 1]

                startDataTime = event0.timeStamp
                endDataTime = event1.timeStamp
                timeInForeground = endDataTime - startDataTime

                if (timeInForeground >= MINIMUM_GET_TIME) {
                    addInTimeUsageInsert(
                        packageName,
                        endDataTime,
                        startDataTime,
                        timeUsageInsertBuffer,
                        packages
                    )
                }
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