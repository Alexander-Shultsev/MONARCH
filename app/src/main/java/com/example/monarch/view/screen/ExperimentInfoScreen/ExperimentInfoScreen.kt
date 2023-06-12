package com.example.monarch.view.screen.TimeUsedScreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.monarch.view.*
import com.example.monarch.view.component.ItemListDoubleVertical
import com.example.monarch.view.theme.*
import com.example.monarch.model.Experiments.ExperimentsData.Apps
import com.example.monarch.model.Experiments.ExperimentsData.ExperimentResults
import com.example.monarch.viewModel.common.DateTime
import com.example.monarch.viewModel.common.dataClass.ItemInfo
import kotlin.collections.ArrayList

@Preview
@Composable
fun ExperimentInfoScreenPreview() {
    val arrayList = arrayListOf("vk", "insta", "twitter")
    val arrayListDays = arrayListOf(
        ExperimentResults("2023-08-12", "2ч 3м 5с"),
        ExperimentResults("2023-08-13", "6ч 3м 5с"),
        ExperimentResults("2023-08-14", "10ч 3м 5с"),
    )
//    ExperimentInfoContent(arrayList, arrayListDays)
}

@Composable
fun ExperimentInfoScreen(
    navController: NavHostController,
    viewModel: ExperimentInfoModel = viewModel()
) {
    val experimentApps = viewModel.experimentApps.observeAsState()
    val experimentResults = viewModel.experimentResults.observeAsState()
    val nameExperiment: String = viewModel.nameExperiment
    val dateStart: String = viewModel.dateStart
    val dateEnd: String = viewModel.dateEnd
    val timeLimit: Int = viewModel.timeLimit

    ExperimentInfoContent(
        experimentApps.value!!,
        experimentResults.value!!,
        nameExperiment,
        dateStart,
        dateEnd,
        timeLimit
    )
}

@Composable
fun ExperimentInfoContent(
    experimentApps: ArrayList<Apps>,
    experimentResults: ArrayList<ExperimentResults>,
    nameExperiment: String,
    dateStart: String,
    dateEnd: String,
    timeLimit: Int,
) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 22.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 19.dp)) {
                Title2(
                    text = nameExperiment,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                H5(
                    text = "$dateStart    -    $dateEnd",
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Title2(
                    text = "Приложения",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    experimentApps.forEach {
                        H5(
                            text = it.appLable,
                            modifier = Modifier.padding(end = 10.dp),
                        )
                    }
                }
                Title2(
                    text = "Лимит",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                H5(
                    text = "${timeLimit}ч",
                    modifier = Modifier.padding(bottom = 20.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Title2(
                        text = "Результаты",
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
        item {
            experimentResults.forEach {
                ItemListDoubleVertical(
                    ItemInfo(
                        topText = it.date,
                        topTextArrangement = Arrangement.Start,
                        bottomText = DateTime.timeFormatter(it.duration.toLong()),
                        bottomTextArrangement = Arrangement.End
                    ),
                    onClick = {},
                    backgroundColor = if (DateTime.getHours(it.duration.toLong()) < timeLimit) Red else Green,
                    textColor = Color.White
                )
            }
        }
    }
}