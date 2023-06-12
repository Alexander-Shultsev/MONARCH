package com.example.monarch.view.screen.AddApplicationToExperiment
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
//import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
//import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
//import androidx.compose.material.Icon
//import androidx.compose.material.Scaffold
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.monarch.presentation.Title2
//import com.example.monarch.presentation.component.MonarchButtonLittle
//import com.example.monarch.presentation.component.MonarchButtonMain
//import com.example.monarch.presentation.screen.TimeUsedScreen.TimeUsedViewModel
//import com.example.monarch.presentation.theme.Orange
//import com.example.monarch.presentation.theme.Purple
//import com.example.monarch.presentation.theme.onPrimaryLight
//import com.example.monarch.repository.Experiments.ExperimentsData.AppsForExperiment
//import org.koin.androidx.compose.getViewModel
//
//
//@Composable
//fun AddApplicationToExperimentScreen(
//    navController: NavController,
//    viewModel: TimeUsedViewModel = getViewModel()
//) {
//    val timeUsageDevice = viewModel.timeUsageDevice.observeAsState()
//    val currentDate = viewModel.currentDate.observeAsState()
//
//
////    AddApplicationToExperimentContent(
////        currentDate.value!!,
////        timeUsageDevice.value!!,
////    )
//}
//
////fun mutableStateOf(value: String, policy: String): Any {
////
////}
//
//@Preview
//@Composable
//fun AddApplicationToExperimentScreen() {
////    var items by remember {
////        com.example.monarch.presentation.screen.AddApplicationToExperiment.mutableStateOf(
////            "", ""
////        )
////    }
//
////    AppsForExperiment("telegram", "idt", false),
////    AppsForExperiment("youtubeasdfasdf", "idy", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegramasdfaasdf", "idt", false),
////    AppsForExperiment("whatsappasdf", "idw", false),
////    AppsForExperiment("telegram", "idt", false),
////    AppsForExperiment("youtubeasdfasdf", "idy", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegram", "idt", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegram", "idt", false),
////    AppsForExperiment("youtube", "idy", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegramasdfasdf", "idt", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegram", "idt", false),
////    AppsForExperiment("youtube", "idy", false),
////    AppsForExperiment("whatsapp", "idw", false),
////    AppsForExperiment("telegram", "idt", false),
//
//    Scaffold(
//        backgroundColor = Purple
//    ) { innerPadding ->
//        AddApplicationToExperimentContent(
//            applications,
//            innerPadding
//        )
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun AddApplicationToExperimentContent(
//    applications: MutableList<AppsForExperiment>,
//    innerPadding: PaddingValues
//) {
//    Column(
//        modifier = Modifier
//            .padding(vertical = 30.dp)
//            .fillMaxWidth()
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 20.dp)
//                .padding(horizontal = 17.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = null,
//                tint = Color.White
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Title2(
//                text = "Добавление приложений",
//            )
//        }
//        LazyHorizontalStaggeredGrid(
//            state = rememberLazyStaggeredGridState(),
//            rows = StaggeredGridCells.Fixed(6),
//            modifier = Modifier
//                .height(270.dp)
//                .padding(horizontal = 17.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//        ) {
//            applications.forEach { elem ->
//                item {
//                    MonarchButtonLittle(
//                        text = elem.appLabel,
//                        onClick = { elem.isSelected = true },
//                        backgroundColor = if (elem.isSelected) Orange else Color.White
//                    )
//                }
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Bottom,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(onPrimaryLight)
//        )
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Bottom,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        MonarchButtonMain(
//            text = "Добавить",
//            icon = Icons.Default.Add,
//            modifier = Modifier
//                .padding(horizontal = 50.dp)
//                .padding(bottom = 20.dp),
//            onClick = { }
//        )
//    }
//}