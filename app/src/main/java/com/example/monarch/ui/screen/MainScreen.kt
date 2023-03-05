package com.example.monarch.ui.screen


import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.monarch.common.DatePicker
import com.example.monarch.common.DateTime
import com.example.monarch.ui.*
import com.example.monarch.ui.theme.*
import com.example.monarch.viewmodel.TimeUsedViewModel
import com.monarchcompany.monarchapp.R
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList


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
                ButtonText(
                    TextData.RequestPermissionScreen.button,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun StateUsageScreen(
    viewModel: TimeUsedViewModel,
    statsManager: UsageStatsManager
) {
    val dateDialogIsVisible = viewModel.dateDialogIsVisible.observeAsState(false)
    val timeUsedInfo = viewModel.timeUsedInfo.observeAsState()
    val currentDate = viewModel.currentDate.observeAsState()
    val animateItem = viewModel.animateItem.observeAsState()
    val count = timeUsedInfo.value!!.size


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
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            currentDate.value?.get("day")?.let { day ->
                currentDate.value?.get("month")?.let { month ->
                    currentDate.value?.get("year")?.let { year ->
                        Title1(
                            text = "$day $month $year",
                            color = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }

            currentDate.value?.get("dayOfWeek")?.let { dayOfWeek ->
                Title1(
                    text = dayOfWeek,
                    color = onPrimaryMedium,
                    modifier = Modifier.padding(start = 7.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                item {
                    for (elem in 0 until count) {
                        AnimatedVisibility(
                            visible = animateItem.value!!
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.secondary)
                                    .fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        )
                                        .fillMaxWidth()
                                ) {
                                    H5(
                                        text = timeUsedInfo.value!![elem].getPackageName(),
                                        modifier = Modifier.padding(bottom = 2.dp),
                                        color = MaterialTheme.colors.primary
                                    )

                                    H6(
                                        text = DateTime.getTime(timeUsedInfo.value!![elem].getTimeInForeground()),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(shape = RoundedCornerShape(topStartPercent = 100))
                .background(MaterialTheme.colors.onPrimary)
                .padding(start = 12.dp, top = 12.dp)
                .clickable {
                    viewModel.changeDateDialogVisible(dateDialogIsVisible.value)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colors.primary
            )
        }
    }

    if (dateDialogIsVisible.value) {
        DatePicker({
            viewModel.onDateSelected(it)
        }, {
            viewModel.closeDialog()
        })
    }
}
