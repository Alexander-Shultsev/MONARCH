package com.example.monarch.view.screen.AddExperiment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.monarch.view.H6
import com.example.monarch.view.Title2
import com.example.monarch.view.component.calendar.DatePicker
import com.example.monarch.view.component.MonarchButtonIcon
import com.example.monarch.view.component.MonarchButtonMain
import com.example.monarch.view.component.MonarchTextFieldNumber
import com.example.monarch.view.component.MonarchTextFieldText
import com.example.monarch.view.component.calendar.CalendarViewModel
import com.example.monarch.view.navigation.MenuItem
import com.example.monarch.view.navigation.NavMainItem
import com.example.monarch.view.navigation.navigateTo
import com.example.monarch.view.theme.TextData
import com.example.monarch.view.theme.onPrimaryLight
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage.Companion.dateOutput
import org.koin.androidx.compose.getViewModel

@Preview
@Composable
fun AddExperimentContentP() {
//    Scaffold(
//        backgroundColor = Purple
//    )
//    { innerPadding ->
//        AddExperimentContent()
//    }
}

@Composable
fun AddExperimentScreen(
    navControllerMain: NavHostController,
    experimentsViewModel: AddExperimentViewModel = getViewModel(),
    calendarViewModel: CalendarViewModel = getViewModel(),
) {
    val dateDialogIsVisible = calendarViewModel.dateDialogIsVisible.observeAsState()
    val dateStartExperiment = experimentsViewModel.dateStartExperiment.observeAsState()
    val dateEndExperiment = experimentsViewModel.dateEndExperiment.observeAsState()
    val description = experimentsViewModel.description.observeAsState()
    val limit = experimentsViewModel.limit.observeAsState()

    AddExperimentContent(
        navControllerMain,
        dateDialogIsVisible.value!!,
        { calendarViewModel.changeDateDialogVisible(it) },
        { experimentsViewModel.setChangeDateTextField(it) },
        { experimentsViewModel.setDateValue(it) },
        dateStartExperiment.value!!,
        dateEndExperiment.value!!,
        description.value!!,
        limit.value!!,
        { experimentsViewModel.setDescription(it) },
        { experimentsViewModel.setLimit(it) },
    )
}

@Composable
fun AddExperimentContent(
    navControllerMain: NavHostController,
    dateDialogIsVisible: Boolean,
    changeDialogVisible: (Boolean) -> Unit,
    setChangeDateTextField: (String) -> Unit,
    setDateValue: (String) -> Unit,
    dateStartExperiment: String,
    dateEndExperiment: String,
    description: String,
    limit: String,
    setDescription: (String) -> Unit,
    setLimit: (String) -> Unit,
) {
    val application = rememberSaveable { mutableListOf(arrayListOf("")) }

    LazyColumn(
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = 17.dp)
            .fillMaxWidth()
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navigateTo(
                            navControllerMain,
                            NavMainItem.NavigationScreen.route
                        )
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Title2(
                    text = "Создание эксперимента",
                )
            }
            MonarchTextFieldText(
                text = description,
                label = "Описание",
                onChange = { setDescription(it) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                MonarchButtonIcon(
                    text = dateStartExperiment,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        changeDialogVisible(dateDialogIsVisible)
                        setChangeDateTextField(AddExperimentViewModel.DateTextField.startDateExperiment)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                MonarchButtonIcon(
                    text = dateEndExperiment,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        changeDialogVisible(dateDialogIsVisible)
                        setChangeDateTextField(AddExperimentViewModel.DateTextField.endDateExperiment)
                    },
                    enable = dateStartExperiment != TextData.AddExperimentsScreen.dateStartExperiment
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            MonarchButtonIcon(
                text = "Добавить приложения",
                icon = Icons.Default.KeyboardArrowRight,
                modifier = Modifier.fillMaxWidth(),
                onClick = {  }
            )
            H6("whatsapp, telegram, youtube")
            Spacer(modifier = Modifier.height(12.dp))
            MonarchTextFieldNumber(
                text = limit,
                label = "Лимит в часах",
                maxChar = 2,
                onChange = { setLimit(it) }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(onPrimaryLight)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MonarchButtonMain(
            text = "Создать",
            icon = Icons.Default.Add,
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .padding(bottom = 20.dp),
            onClick = { }
        )
    }

    if (dateDialogIsVisible) {
        DatePicker(
            { setDateValue(dateOutput.format(it)) },
            { changeDialogVisible(dateDialogIsVisible) },
        )
    }
}