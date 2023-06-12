package com.example.monarch.view.screen.TimeUsedScreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.monarch.view.component.calendar.DatePicker
import com.example.monarch.viewModel.common.DateTime
import com.example.monarch.viewModel.common.DateTime.Companion.getDateString
import com.example.monarch.view.*
import com.example.monarch.view.component.ActionButton
import com.example.monarch.view.component.ItemListDoubleVertical
import com.example.monarch.view.theme.*
import com.example.monarch.viewModel.common.dataClass.ItemInfo
import org.koin.androidx.compose.getViewModel

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
fun TimeUsedScreen(
    navController: NavController,
    viewModel: TimeUsedViewModel = getViewModel()
) {
    val dateDialogIsVisible = viewModel.dateDialogIsVisible.observeAsState(false)
    val timeUsageDevice = viewModel.timeUsageDevice.observeAsState()
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
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val dateString = getDateString(currentDate.value!!)

            Title1(
                text = "${dateString.day} ${dateString.month} ${dateString.year}",
                color = MaterialTheme.colors.onPrimary,
            )

            Title1(
                text = dateString.dayOfWeek,
                color = onPrimaryMedium,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                for (elem in timeUsageDevice.value!!) {
                    ItemListDoubleVertical(
                        ItemInfo(
                            topText = elem.appLabel,
                            topTextArrangement = Arrangement.Start,
                            bottomText = DateTime.timeFormatter(elem.duration),
                            bottomTextArrangement = Arrangement.End
                        ),
                        onClick = {}
                    )
                }
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    ActionButton(
        icon = Icons.Default.DateRange,
        onClick = { viewModel.changeDateDialogVisible(dateDialogIsVisible.value) }
    )

    if (dateDialogIsVisible.value) {
        DatePicker(
            { viewModel.onDateSelected(it) },
            { viewModel.changeDateDialogVisible(dateDialogIsVisible.value) },
        )
    }
}

@Preview
@Composable
fun StateUsageScreenPreview() {
    TimeUsedScreen(rememberNavController())
}
