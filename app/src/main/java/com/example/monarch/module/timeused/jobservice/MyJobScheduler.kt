package com.example.monarch.module.timeused.jobservice

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast
import com.example.monarch.module.timeused.TimeUsedModule
import com.example.monarch.module.timeused.data.Constant


class MyJobScheduler : JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean {
        Toast.makeText(applicationContext,"Job Stopped", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        val timeUsageViewModel = TimeUsedModule()
        timeUsageViewModel.getStateUsageFromEvent(Constant.TODAY_DATE)

        return false
    }
}