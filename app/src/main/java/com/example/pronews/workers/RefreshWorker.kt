package com.example.pronews.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters


class RefreshWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        Log.v("KEK size lol", "RefreshWorker")

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
