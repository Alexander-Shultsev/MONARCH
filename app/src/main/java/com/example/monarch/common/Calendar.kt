package com.example.monarch.common

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.monarch.ui.Subtitle1
import com.monarchcompany.monarchapp.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePicker(onDateSelected: (Date) -> Unit, closeDialog: () -> Unit) {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    val date = formatter.parse("01.01.2023")

    val selDate = remember { mutableStateOf(date) }

    //todo - add strings to resource after POC
    Dialog(onDismissRequest = { closeDialog() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            CustomCalendarView(onDateSelected = {
                selDate.value = it
            })

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = closeDialog
                ) {
                    //TODO - hardcode string
                    Subtitle1(
                        text = "Передумал",
                        color = MaterialTheme.colors.onSurface
                    )
                }

                TextButton(
                    onClick = {
                        closeDialog()
                        onDateSelected(selDate.value)
                    }
                ) {
                    //TODO - hardcode string
                    Subtitle1(
                        text = "Покажи",
                        color = MaterialTheme.colors.onSurface
                    )
                }

            }
        }
    }
}

@Composable
private fun CustomCalendarView(onDateSelected: (Date) -> Unit) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, R.style.Theme_Monarch))
        },
        update = { view ->
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("RU"))
            val minDate = formatter.parse("01.01.1970").time
            val maxDate = Calendar.getInstance().timeInMillis

            view.minDate = minDate
            view.maxDate = maxDate
            view.firstDayOfWeek = Calendar.MONDAY // понедельник - первый день недели

            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val dateString = "$dayOfMonth.${month + 1}.$year"
                val date = formatter.parse(dateString)

                onDateSelected(date!!)
            }
        }
    )
}