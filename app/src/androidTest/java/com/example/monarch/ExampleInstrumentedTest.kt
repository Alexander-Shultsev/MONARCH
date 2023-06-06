package com.example.monarch

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.monarch.module.common.App
import com.example.monarch.module.timeused.TimeUsedModule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.text.SimpleDateFormat

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val viewModel = TimeUsedModule()

        val date = SimpleDateFormat("dd-MM-yyyy").parse("10-03-2023")
        val statsManager: UsageStatsManager =
            App.getContextInstance().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

//        viewModel.getStateUsageFromEvent(date!!, statsManager)
    }
}