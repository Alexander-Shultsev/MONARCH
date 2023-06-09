package com.example.monarch.presentation.screen.TimeUsedScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.monarch.owner
import com.example.monarch.repository.TimeUsage.ExperimentsQuery
import com.example.monarch.repository.dataClass.Experiments.Apps
import com.example.monarch.repository.dataClass.Experiments.ExperimentResults


class ExperimentInfoModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val experimentsQuery = ExperimentsQuery()

    // список приложений для эксперимента
    private val _experimentApps: MutableLiveData<ArrayList<Apps>> = MutableLiveData()
    val experimentApps: LiveData<ArrayList<Apps>> = _experimentApps

    // список приложений для получения результатов с сервера
    private var appsToInsert: ArrayList<String> = arrayListOf()

    // результаты эксперимента
    private val _experimentResults: MutableLiveData<ArrayList<ExperimentResults>> = MutableLiveData()
    val experimentResults: LiveData<ArrayList<ExperimentResults>> = _experimentResults

    private val idExperiment: Long = savedStateHandle["idExperiment"]!!
    val nameExperiment: String = savedStateHandle["nameExperiment"]!!
    val dateStart: String = savedStateHandle["dateStart"]!!
    val dateEnd: String = savedStateHandle["dateEnd"]!!
    val timeLimit: Int = savedStateHandle["timeLimit"]!!

    init {
        getExperimentResults()
        getAppForExperiment()

        _experimentApps.value = arrayListOf()
        _experimentResults.value = arrayListOf()
    }

    // получить список приложений для эксперимента
    private fun getAppForExperiment() {
        experimentsQuery.getAppForExperiment(_experimentApps, 1, idExperiment)
    }

    // получить список результатов
    private fun getExperimentResults() {
        experimentApps.observe(owner) {
            experimentApps.value!!.forEach {
                appsToInsert.add(it.appNameId)
            }

            experimentsQuery.getExperimentResult(
                _experimentResults,
                1,
                dateStart,
                dateEnd,
                appsToInsert
            )
        }
    }

}