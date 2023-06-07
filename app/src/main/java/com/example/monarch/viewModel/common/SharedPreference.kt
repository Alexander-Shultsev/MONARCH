package com.example.monarch.viewModel.common

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log

private const val SHARED_PREFERENCE_NAME = "PERMISSION"

class SharedPreference(context: Context) {

    private val sharedPreference =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun save(
        key: String,
        value: Boolean
    ) {
        Log.i(TAG, "key - $key, value - $value")
        sharedPreference.edit().putBoolean(key, value).apply()
    }

    fun get(key: String): Boolean {
        Log.i(TAG, "key - $key")
        return sharedPreference.getBoolean(key, false)
    }
}