package com.example.monarch.model.TimeUsage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monarch.viewModel.common.Constant.SERVER_TAG
import com.example.monarch.model.RetrofitInstance.Companion.serviceTimeUsage
import com.example.monarch.model.TimeUsage.TimeUsageData.TimeUsageDevice
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

                Log.i(SERVER_TAG, "postTimeUsage: $code")
                Log.i(SERVER_TAG, "postTimeUsage1: $text")
            }
            catch (e: Exception) {
                Log.i(SERVER_TAG, "postTimeUsageError: $e")
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
                val result = serviceTimeUsage.getTimeUsageDevice(date, 1)
                val code = result.code()
                responceDevice.value = result.body()

                Log.i(SERVER_TAG, "postTimeUsage: $code")
                Log.i(SERVER_TAG, "postTimeUsage1: ${responceDevice.value}")
            } catch (e: Exception) {
                Log.i(SERVER_TAG, "postTimeUsageError: $e")
            }
        }
    }
}