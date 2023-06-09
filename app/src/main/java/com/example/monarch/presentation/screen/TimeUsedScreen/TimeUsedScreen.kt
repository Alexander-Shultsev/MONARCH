package com.example.monarch.presentation.screen.TimeUsedScreen


import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.monarch.presentation.component.DatePicker
import com.example.monarch.viewModel.common.DateTime
import com.example.monarch.viewModel.common.DateTime.Companion.getDateString
import com.example.monarch.presentation.*
import com.example.monarch.presentation.component.ItemListDoubleVertical
import com.example.monarch.presentation.theme.*
import com.example.monarch.viewModel.common.dataClass.ItemInfo
import com.monarchcompany.monarchapp.R
import org.koin.androidx.compose.getViewModel
import java.util.*

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

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("MutableCollectionMutableState")
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
        DatePicker(
            { viewModel.onDateSelected(it) },
            { viewModel.changeDateDialogVisible(dateDialogIsVisible.value) },
            currentDate.value!!.time
        )
    }
}

@Preview
@Composable
fun StateUsageScreenPreview() {
    TimeUsedScreen(rememberNavController())
}
