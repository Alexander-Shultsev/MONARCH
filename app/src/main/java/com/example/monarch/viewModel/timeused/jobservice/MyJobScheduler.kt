package com.example.monarch.viewModel.timeused.jobservice

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import com.example.monarch.view.screen.TimeUsedScreen.TimeUsedViewModel
import com.example.monarch.viewModel.timeused.data.ConstantTimeUsage


class MyJobScheduler : JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean {
        Toast.makeText(applicationContext,"Job Stopped", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        val timeUsageViewModel = TimeUsedViewModel()
        timeUsageViewModel.getStateUsageFromEvent(ConstantTimeUsage.TODAY_DATE)

        return false
    }
}