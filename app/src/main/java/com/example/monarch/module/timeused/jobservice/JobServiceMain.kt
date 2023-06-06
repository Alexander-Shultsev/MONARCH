package com.example.monarch.module.timeused.jobservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.monarch.module.common.App.Companion.getContextInstance
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.TIME_FOR_COLLECT
import com.example.monarch.module.timeused.data.ConstantTimeUsage.Companion.UNIT_OF_MEASUREMENT_FOR_JOB_SCHEDULER
import java.util.concurrent.TimeUnit

class JobServiceMain {

    private val timeUsagePostJobId = 1

    private var jobScheduler =
        getContextInstance().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    private var component =
        ComponentName(getContextInstance(), MyJobScheduler::class.java)

    fun startJob() {
        var jobIsScheduler: Int = -1

        if (!isJobServiceRunning(getContextInstance(), timeUsagePostJobId)) {
            val timeUsagePostJob = JobInfo.Builder(timeUsagePostJobId, component)
                .setPeriodic(UNIT_OF_MEASUREMENT_FOR_JOB_SCHEDULER.toMillis(TIME_FOR_COLLECT * 1L))// период выполнения (минимум 15 мин)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // тип сети
                .setPersisted(true) // чтобы сервис продолжал работать даже после перезагрузки
                .build()

            jobIsScheduler = jobScheduler.schedule(timeUsagePostJob)
        }
        checkJobStart(jobIsScheduler)
    }

    private fun checkJobStart(jobIsScheduler: Int) {
        if (jobIsScheduler == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "startJob: jobIsStart")
        } else {
            Log.i(TAG, "startJob: jobStartAlready")
        }
    }

    private fun isJobServiceRunning(
        context: Context, jobId: Int
    ): Boolean {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        for (jobInfo in scheduler.allPendingJobs) {
            if (jobInfo.id == jobId) {
                return true
            }
        }
        return false
    }

    fun stopJob(jobId: Int) {
        jobScheduler.cancel(jobId)
    }
}