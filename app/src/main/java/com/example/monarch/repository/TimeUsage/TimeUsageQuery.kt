package com.example.monarch.repository.TimeUsage

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monarch.module.common.Constant.SERVER
import com.example.monarch.repository.RetrofitInstance.Companion.serviceTimeUsage
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageDevice
import kotlinx.coroutines.launch
import java.lang.Exception

class TimeUsageQuery: ViewModel() {

    // добавление данных на сервер о времени использования устройства
    fun postTimeUsage(
        startDateTime: ArrayList<String>,
        endDateTime: ArrayList<String>,
        appLabel: ArrayList<String>,
        appNameId: ArrayList<String>,
        fkUser: ArrayList<Int>,
    ) {
        viewModelScope.launch {
            try {
                val result = serviceTimeUsage.postTimeUsage(
                    startDateTime,
                    endDateTime,
                    appLabel,
                    appNameId,
                    fkUser
                )
                val code = result.code()
                val text  = result.body()!!.response

                Log.i(SERVER, "postTimeUsage: $code")
                Log.i(SERVER, "postTimeUsage1: $text")
            }
            catch (e: Exception) {
                Log.i(SERVER, "postTimeUsageError: $e")
            }
        }
    }

    // получить данные с сервера о времени использования устройства агрегированные по приложениям
    fun getTimeUsageDevice(
        date: String,
        responceDevice: MutableLiveData<ArrayList<TimeUsageDevice>>
    ) {
        viewModelScope.launch {
            try {
                val result = serviceTimeUsage.getTimeUsageDevice(date)
                val code = result.code()
                responceDevice.value = result.body()

                Log.i(SERVER, "postTimeUsage: $code")
                Log.i(SERVER, "postTimeUsage1: ${responceDevice.value}")
            } catch (e: Exception) {
                Log.i(SERVER, "postTimeUsageError: $e")
            }
        }
    }
}