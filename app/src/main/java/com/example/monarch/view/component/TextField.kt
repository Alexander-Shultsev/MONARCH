package com.example.monarch.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.monarch.view.Title1
import com.example.monarch.view.theme.Orange
import com.example.monarch.view.theme.onPrimaryLight


@Composable
fun MonarchTextFieldText(
    text: String,
    label: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Title1(label, color = onPrimaryLight) },
        value = text,
        onValueChange = { onChange(it) },
        shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            unfocusedBorderColor = Orange,
            focusedBorderColor = Orange,
            disabledBorderColor = Orange,
            cursorColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MonarchTextFieldNumber(
    text: String,
    label: String,
    maxChar: Int,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        label = { Title1(label, color = onPrimaryLight) },
        value = text,
        onValueChange = {
            if (it.length > maxChar){
                onChange(it.slice(0 until maxChar))
            } else {
                onChange(it)
            }
        },
        shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            unfocusedBorderColor = Orange,
            focusedBorderColor = Orange,
            disabledBorderColor = Orange,
            cursorColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}