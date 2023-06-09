package com.example.monarch.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monarch.presentation.H5
import com.example.monarch.presentation.H6
import com.example.monarch.presentation.theme.Orange
import com.example.monarch.presentation.theme.Purple
import com.example.monarch.viewModel.common.dataClass.ItemInfo

@Composable
fun ItemListDoubleVertical(
    itemInfo: ItemInfo,
    onClick: () -> Unit,
    backgroundColor: Color = Orange,
    textColor: Color = MaterialTheme.colors.primary,
) {
    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
                .fillMaxWidth()
                .clickable { onClick() }) {
            Row(
                horizontalArrangement = itemInfo.topTextArrangement,
                modifier = Modifier.fillMaxWidth()) {
                H5(
                    text = itemInfo.topText,
                    modifier = Modifier.padding(bottom = 2.dp),
                    color = textColor,
                )
            }

            Row(
                horizontalArrangement = itemInfo.bottomTextArrangement,
                modifier = Modifier.fillMaxWidth()) {
                H6(
                    text = itemInfo.bottomText,
                    color = textColor
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(5.dp))
}