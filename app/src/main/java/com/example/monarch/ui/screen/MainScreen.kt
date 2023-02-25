package com.example.monarch.ui.screen

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monarch.common.DateTime
import com.example.monarch.common.MaskTransformation
import com.example.monarch.ui.theme.MonarchTheme
import com.example.monarch.viewmodel.TimeUsedViewModel
import java.sql.Statement

@Composable
fun MainScreen(
    statsManager: UsageStatsManager,
    appOpsManager: AppOpsManager,
    packageName: String,
    viewModel: TimeUsedViewModel = TimeUsedViewModel()
) {
    val inputDate = remember { mutableStateOf("") }
    val timeUsedInfo = viewModel.timeUsedInfo.observeAsState()
    viewModel.isUsageStatsPermission(statsManager, appOpsManager, packageName)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        var time: String

        Column {
            OutlinedTextField(
                value = inputDate.value,
                onValueChange = { it ->
                    if (it.length <= 8) {
                        inputDate.value = it.filter { it.isDigit() }
                        if (it.length == 8) {

                        }
                    }
                },
                placeholder = { Text(text = "____.__.__") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = MaskTransformation(),
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
                        for (elem in timeUsedInfo.value!!) {
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
    }
}