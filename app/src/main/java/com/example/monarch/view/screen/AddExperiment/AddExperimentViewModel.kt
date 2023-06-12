package com.example.monarch.view.screen.AddExperiment

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monarch.model.TimeUsage.ExperimentsQuery
import com.example.monarch.model.Experiments.ExperimentsData.Experiments
import com.example.monarch.view.theme.TextData
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.dateOutput
import java.util.Date

class AddExperimentViewModel: ViewModel() {

    private val experimentsQuery = ExperimentsQuery()

    // список экспериментов
    private val _experiments: MutableLiveData<ArrayList<Experiments>> = MutableLiveData()
    val experiments: LiveData<ArrayList<Experiments>> = _experiments

    // поле дата начала эксперимента
    private val _dateStartExperiment: MutableLiveData<String> = MutableLiveData()
    val dateStartExperiment: LiveData<String> = _dateStartExperiment

    // поле дата окончания эксперимента
    private val _limit: MutableLiveData<String> = MutableLiveData()
    val limit: LiveData<String> = _limit

    // поле дата окончания эксперимента
    private val _description: MutableLiveData<String> = MutableLiveData()
    val description: LiveData<String> = _description

    // поле дата окончания эксперимента
    private val _dateEndExperiment: MutableLiveData<String> = MutableLiveData()
    val dateEndExperiment: LiveData<String> = _dateEndExperiment

    init {
        getExperiments()
        _experiments.value = arrayListOf()
        _dateStartExperiment.value = TextData.AddExperimentsScreen.dateStartExperiment
        _description.value = ""
        _limit.value = ""
        _dateEndExperiment.value = TextData.AddExperimentsScreen.dateEndExperiment
    }

    object DateTextField {
        val startDateExperiment = "startDateExperiment"
        val endDateExperiment = "endDateExperiment"
    }
    private var changeDateTextField: String = ""

    private fun getExperiments() {
        experimentsQuery.getExperiments(_experiments)
    }

    fun setChangeDateTextField(changeDateTextFieldValue: String) {
        changeDateTextField = changeDateTextFieldValue
    }

    fun setDescription(descriptionValue: String) {
        _description.value = descriptionValue
    }

    fun setLimit(limitValue: String) {
        _limit.value = limitValue
    }

    fun setDateValue(date: String) {
        when(changeDateTextField) {
            DateTextField.startDateExperiment -> {
                _dateStartExperiment.value = date
            }
            DateTextField.endDateExperiment -> {
                _dateEndExperiment.value = date
            }
        }
    }
}