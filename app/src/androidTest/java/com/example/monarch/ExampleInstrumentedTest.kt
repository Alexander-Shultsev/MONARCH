package com.example.monarch

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.monarch.module.timeused.TimeUsedModule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val usedStatManager = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val viewModel = TimeUsedModule()
    }
}