package com.example.monarch.view.screen.RequestPermissionGetStateUsageScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.monarch.view.ButtonText
import com.example.monarch.view.Subtitle1
import com.example.monarch.view.theme.Dimention
import com.example.monarch.view.theme.ShapesMain
import com.example.monarch.view.theme.TextData
import com.example.monarch.view.theme.onPrimaryMedium
import com.example.monarch.viewModel.permission.PermissionViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun RequestPermissionGetStateUsageScreen(
    permissionViewModel: PermissionViewModel = getViewModel()
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
                        permissionViewModel.isUsageStatsPermission()
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