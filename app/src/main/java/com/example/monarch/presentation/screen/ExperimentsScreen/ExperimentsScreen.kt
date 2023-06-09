package com.example.monarch.presentation.screen.ExperimentsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.monarch.presentation.component.ItemListDoubleVertical
import com.example.monarch.presentation.navigation.NavMainItem
import com.example.monarch.presentation.navigation.navigateTo
import com.example.monarch.viewModel.common.dataClass.ItemInfo
import org.koin.androidx.compose.getViewModel

@Composable
fun ExperimentsScreen(
    navControllerMain: NavHostController,
    experimentsViewModel: ExperimentsViewModel = getViewModel()
) {
    val experiments = experimentsViewModel.experiments.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            for (elem in experiments.value!!) {
                ItemListDoubleVertical(
                    ItemInfo(
                        topText = elem.dateStart,
                        topTextArrangement = Arrangement.Start,
                        bottomText = elem.name,
                        bottomTextArrangement = Arrangement.Start
                    ),
                    onClick = {
                       navigateTo(
                           navControllerMain,
                           "${NavMainItem.ExperimentInfoScreen.route}/${elem.idExperiment}/${elem.name}/${elem.dateStart}/${elem.dateEnd}/${elem.timeLimit}"
                       )
                    }
                )
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}