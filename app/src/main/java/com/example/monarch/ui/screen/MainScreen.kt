package com.example.monarch.ui.screen

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.monarch.common.DatePicker
import com.example.monarch.common.DateTime
import com.example.monarch.viewmodel.TimeUsedViewModel
import com.example.monarch.viewmodel.TimeUsedViewModel.Companion.DEFAULT_DATE

@Composable
fun MainScreen(
    statsManager: UsageStatsManager,
    appOpsManager: AppOpsManager,
    packageName: String,
    viewModel: TimeUsedViewModel,
    activity: Activity
) {
    val inputDate = remember { mutableStateOf(DEFAULT_DATE) }
    val dateDialogIsVisible = viewModel.dateDialogIsVisible.observeAsState(false)

    val timeUsedInfo = viewModel.timeUsedInfo.observeAsState(arrayListOf())
    viewModel.isUsageStatsPermission(statsManager, appOpsManager, packageName)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        var time: String

        Column {
            OutlinedTextField(
                value = inputDate.value,
                onValueChange = { dateText ->
                    inputDate.value = dateText
                    if (dateText.length == 10) {
                        viewModel.updateDate(dateText, statsManager, activity)
                    }
                },
                placeholder = { Text(text = "дд.мм.гггг") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                visualTransformation = MaskTransformation(),
            )
        }
        LazyColumn(
            modifier = Modifier.padding(top = 60.dp),
            content = {
                item {
                    Column(
                        modifier = Modifier.padding(
                            vertical = 24.dp,
                            horizontal = 24.dp
                        )
                    ) {
                        for (elem in timeUsedInfo.value) {
                            Text(
                                text = elem.getPackageName(),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            Text(
                                text = DateTime.getTime(elem.getTimeInForeground()),
                                modifier = Modifier.padding(bottom = 7.dp)
                            )
                        }
                    }
                }
            }
        )
        if (dateDialogIsVisible.value) {
            DatePicker({ viewModel.onDateSelected(it) }, { viewModel.closeDialog() })
        }

        Button(
            onClick = { viewModel.changeDateDialogVisible(dateDialogIsVisible.value) }
        ) {
            Text(text = "Изменить дату")
        }
    }
}