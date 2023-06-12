package com.example.monarch.model.TimeUsage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monarch.model.RetrofitInstance.Companion.serviceExperiments
import com.example.monarch.viewModel.common.Constant.SERVER_TAG
import com.example.monarch.model.RetrofitInstance.Companion.serviceTimeUsage
import com.example.monarch.model.Experiments.ExperimentsData.Apps
import com.example.monarch.model.Experiments.ExperimentsData.ExperimentResults
import com.example.monarch.model.Experiments.ExperimentsData.Experiments
import kotlinx.coroutines.launch
import java.lang.Exception

class ExperimentsQuery: ViewModel() {

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

    // получить список экспериментов
    fun getExperiments(
        responceExperiments: MutableLiveData<ArrayList<Experiments>>
    ) {
        viewModelScope.launch {
            try {
                val result = serviceExperiments.getExperiments(1)
                val code = result.code()
                responceExperiments.value = result.body()

                Log.i(SERVER_TAG, "postTimeUsage: $code")
                Log.i(SERVER_TAG, "postTimeUsage1: ${responceExperiments.value}")
            } catch (e: Exception) {
                Log.i(SERVER_TAG, "postTimeUsageError: $e")
            }
        }
    }

    // получить список приложений для эксперимента
    fun getAppForExperiment(
        responceAppForExperiment: MutableLiveData<ArrayList<Apps>>,
        fkUser: Long,
        fkExperiments: Long,
    ) {
        viewModelScope.launch {
            try {
                val result = serviceExperiments.getAppForExperiment(fkUser, fkExperiments)
                val code = result.code()
                responceAppForExperiment.value = result.body()

                Log.i(SERVER_TAG, "postTimeUsage: $code")
                Log.i(SERVER_TAG, "postTimeUsage1: ${responceAppForExperiment.value}")
            } catch (e: Exception) {
                Log.i(SERVER_TAG, "postTimeUsageError: $e")
            }
        }
    }

    // получить список результатов
    fun getExperimentResult(
        responceExperimentResult: MutableLiveData<ArrayList<ExperimentResults>>,
        fkUser: Int,
        dateStart: String,
        dateEnd: String,
        appNameId: ArrayList<String>,
    ) {
        viewModelScope.launch {
            try {
                val result = serviceExperiments.getExperimentResult(
                    fkUser,
                    dateStart,
                    dateEnd,
                    appNameId
                )
                val code = result.code()
                responceExperimentResult.value = result.body()

                Log.i(SERVER_TAG, "postTimeUsage: $code")
                Log.i(SERVER_TAG, "postTimeUsage1: ${responceExperimentResult.value}")
            } catch (e: Exception) {
                Log.i(SERVER_TAG, "postTimeUsageError: $e")
            }
        }
    }
}