package com.example.monarch

import com.example.monarch.viewModel.common.DateTime.Companion.timeFormatter
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(timeFormatter(1678377773150), "19ч 2м 53с")
    }
}