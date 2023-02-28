package com.example.monarch.common

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable

fun showMessage(
    message: String,
    activity: Activity
) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}