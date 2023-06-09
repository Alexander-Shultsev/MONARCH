package com.example.monarch.presentation.screen.ExperimentsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.repository.TimeUsage.ExperimentsQuery
import com.example.monarch.repository.dataClass.Experiments.Experiments

class ExperimentsViewModel: ViewModel() {

    private val experimentsQuery = ExperimentsQuery()

    // список экспериментов
    private val _experiments: MutableLiveData<ArrayList<Experiments>> = MutableLiveData()
    val experiments: LiveData<ArrayList<Experiments>> = _experiments

    init {
        getExperiments()

        _experiments.value = arrayListOf()
    }

    private fun getExperiments() {
        experimentsQuery.getExperiments(_experiments)
    }

}