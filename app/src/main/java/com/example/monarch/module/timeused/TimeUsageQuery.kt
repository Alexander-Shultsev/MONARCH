package com.example.monarch.module.timeused

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monarch.repository.RetrofitInstance.Companion.serviceTimeUsage
import com.example.monarch.repository.dataClass.TimeUsage.TimeUsageInsert
import kotlinx.coroutines.launch
import java.lang.Exception

class TimeUsageQuery: ViewModel() {

    // Добавление информации о времени использования смартфона
    private val _responseCode: MutableLiveData<Int> = MutableLiveData()
    val responseCode: LiveData<Int> = _responseCode

    // Добавление информации о времени использования смартфона
    private val _body: MutableLiveData<String> = MutableLiveData()
    val body1: LiveData<String> = _body

    fun postTimeUsage(
        startDateTime: ArrayList<String>,
        endDateTime: ArrayList<String>,
        appLabel: ArrayList<String>,
        appNameId: ArrayList<String>,
        fkUser: ArrayList<Int>,
    ) {
        viewModelScope.launch {
            try {
                val result = serviceTimeUsage.postTimeUsage1(
                    startDateTime,
                    endDateTime,
                    appLabel,
                    appNameId,
                    fkUser
                )
                _responseCode.value = result.code()
                _body.value = result.body()!!.response

                Log.i(TAG, "postTimeUsage: ${responseCode.value}")
                Log.i(TAG, "postTimeUsage1: ${body1.value}")
            }
            catch (e: Exception) { Log.i(TAG, "postTimeUsageError: $e") }
        }
    }

}