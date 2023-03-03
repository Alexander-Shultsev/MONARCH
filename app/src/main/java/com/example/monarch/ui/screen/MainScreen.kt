package com.example.monarch.ui.screen


import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.monarch.common.DatePicker
import com.example.monarch.common.DateTime
import com.example.monarch.module.TimeUsed
import com.example.monarch.ui.ButtonText
import com.example.monarch.ui.H2
import com.example.monarch.ui.H4
import com.example.monarch.ui.Subtitle1
import com.example.monarch.ui.theme.*
import com.example.monarch.viewmodel.TimeUsedViewModel
import java.util.*
import kotlin.collections.HashMap


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    statsManager: UsageStatsManager,
    appOpsManager: AppOpsManager,
    packageName: String,
    viewModel: TimeUsedViewModel
) {
    val currentUsageStatsPermission = viewModel.checkUsageStatsPermission(
        appOpsManager,
        packageName
    )
    val stateUsagePermissionGranted =
        viewModel.stateUsagePermission.observeAsState(currentUsageStatsPermission)

    if (stateUsagePermissionGranted.value) {
        viewModel.getStateUsageFromEvent(
            statsManager,
            TimeUsedViewModel.DEFAULT_DATE
        )
        StateUsageScreen(viewModel, statsManager)
    } else {
        RequestPermissionGetStateUsageScreen(
            viewModel,
            statsManager,
            appOpsManager,
            packageName
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewRequestPermissionGetStateUsageScreen() {
//    MonarchTheme {
//        Surface(
//            color = MaterialTheme.colors.primary,
//            modifier = Modifier.fillMaxHeight()
//        ) {
//            StateUsageScreen()
//        }
//    }
//}

@Composable
fun RequestPermissionGetStateUsageScreen(
    viewModel: TimeUsedViewModel,
    statsManager: UsageStatsManager,
    appOpsManager: AppOpsManager,
    packageName: String
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.onPrimary,
                        shape = ShapesMain.medium
                    )
            ) {
                Subtitle1(
                    text = TextData.RequestPermissionScreen.text,
                    modifier = Modifier
                        .padding(Dimention.TextBlock.padding)
                )
            }

            Box(
                Modifier
                    .width(2.dp)
                    .height(120.dp)
                    .background(onPrimaryMedium)
            )

            Box(
                modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topStartPercent = 1,
                            topEndPercent = 50,
                            bottomEndPercent = 1,
                            bottomStartPercent = 50
                        )
                    )
                    .background(MaterialTheme.colors.onPrimary)
                    .clickable {
                        viewModel.isUsageStatsPermission(statsManager, appOpsManager, packageName)
                    }
                    .padding(vertical = 16.dp, horizontal = 40.dp),
            ) {
                ButtonText(TextData.RequestPermissionScreen.button, color = MaterialTheme.colors.primary)
            }
        }
    }
}

@Composable
fun StateUsageScreen(
    viewModel: TimeUsedViewModel,
    statsManager: UsageStatsManager
) {
    val dateDialogIsVisible = viewModel.dateDialogIsVisible.observeAsState(false)
    val timeUsedInfo = viewModel.timeUsedInfo.observeAsState()
    val currentDate = viewModel.currentDate.observeAsState()

//    val timeUsedInfo = arrayListOf(
//        TimeUsed(
//            packageName = "com.example.monarch",
//            position = 0,
//            timeInForeground = 1000000
//        ),
//        TimeUsed(
//            packageName = "com.example.rustore",
//            position = 1,
//            timeInForeground = 1010000
//        ),
//        TimeUsed(
//            packageName = "com.example.timetable",
//            position = 2,
//            timeInForeground = 1030000
//        )
//    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .padding(vertical = 20.dp, horizontal = Dimention.Main.paddingMain)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            currentDate.value?.get("dayOfWeek")?.let { dayOfWeek ->
                H4(
                    text = dayOfWeek,
                    color = onPrimaryMedium,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            currentDate.value?.get("day")?.let { day ->
                H2(
                    text = day,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            currentDate.value?.get("month")?.let { month ->
                currentDate.value?.get("year")?.let { year ->
                    H4(
                        text = "$month $year",
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Button(
            onClick = {
                viewModel.changeDateDialogVisible(dateDialogIsVisible.value)
            },
        ) {
            Text(text = "Изменить дату")
        }
    }

    if (dateDialogIsVisible.value) {
        DatePicker({
            viewModel.onDateSelected(it) }, { viewModel.closeDialog()
        })
    }
}
