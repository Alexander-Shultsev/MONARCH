package com.example.monarch.view.screen.AddApplicationToExperiment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.model.TimeUsage.ExperimentsQuery
import com.example.monarch.model.Experiments.ExperimentsData.Experiments

class AddApplicationToExperimentViewModel: ViewModel() {

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