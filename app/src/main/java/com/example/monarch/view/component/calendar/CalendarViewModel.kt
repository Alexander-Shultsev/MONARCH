package com.example.monarch.view.component.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

    class CalendarViewModel: ViewModel() {

    // отображение диалогового окна
    private val _dateDialogIsVisible: MutableLiveData<Boolean> = MutableLiveData()
    val dateDialogIsVisible: LiveData<Boolean> = _dateDialogIsVisible

    init {
        _dateDialogIsVisible.value = false
    }

    // изменение видимости диалогового окна
    fun changeDateDialogVisible(isVisible: Boolean) {
        _dateDialogIsVisible.value = !isVisible
    }
}