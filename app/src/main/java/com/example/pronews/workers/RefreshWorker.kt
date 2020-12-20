package com.example.pronews.workers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pronews.utils.NewsData
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io


// Define the parameter keys:
const val CATEGORY_ARG = "CATEGORY_ARG"
const val LANGUAGE_ARG = "LANGUAGE_ARG"

// ...and the result key:
const val KEY_RESULT = "result"

class RefreshWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val category = inputData.getString(CATEGORY_ARG)
        val language = inputData.getString(LANGUAGE_ARG)

        if (language == null || category == null) {
            return Result.failure()
        }

        var kek = false

        NewsData
            .updateWorker("business", language) // хардкод для демонстрации изменений
            .blockingForEach { res ->
                run {
                    res.List.map { elem ->
                        NewsData.newsSetWorker.add(elem)
                    }
                    kek = checkIfHasNews();
                }
            }
        Log.v("KEK kek", kek.toString())
        return Result.success(workDataOf(KEY_RESULT to kek))
    }

    private fun checkIfHasNews(): Boolean {
        if (NewsData.newsSetWorker[0] != NewsData.getData()[0]) {
            return true
        }
        return false
    }

    private fun newDataAppear() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable { // Run your task here
//            Snackbar.make(contextView, R.string.text_label, Snackbar.LENGTH_LONG)
//                .setAction(R.string.action_text) {
//                    // Responds to click on the action
//                }
//                .show()
            Toast.makeText(
                applicationContext,
                "Testingfn,sfndfgsnggdfs njgsdfssfgskgbfgfsfggs jbsfgjfsbgjksfbgjfbgkjs bdgjksfbgjkf bgknfbgksdf gbksjfgbkjsfbgkjsfbg",
                Toast.LENGTH_SHORT
            ).show()
        }, 1000)
    }
}
