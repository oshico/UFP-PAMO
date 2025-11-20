package edu.ufp.pam.examples.p05_farrusco.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import okhttp3.OkHttpClient
import okhttp3.Request

class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    /**
     * CoroutineWorker.doWork() is a suspending fun which by defaults runs on Dispatchers.Default.
     * It is possible to change the Coroutine Dispatcher to, e.g., Dispatchers.IO.
     */
    //override val coroutineContext = Dispatchers.IO
    //override suspend fun doWork(): Result = coroutineScope { ... }

    /**
     * The Worker will be scheduled by WorkManager, i.e., the doWork() runs synchronously on a
     *  background thread provided by WorkManager.
     * The Result returned by doWork() will be:
     *  - Result.success(): work finished successfully.
     *  - Result.failure(): work failed.
     *  - Result.retry(): work failed and should be tried at another time according to retry policy.
     */
    override fun doWork(): Result {
        val resourceUri = inputData.getString(WorkManagerHelper.KEY_WORKER_DOWNLOAD_INPUT_URL)
        Log.e(this.javaClass.simpleName, "doWork(): resourceUri=$resourceUri")

        //Execute download HTTP call
        val response = resourceUri?.let {
            downloadWorkerSyncHttpCall(it)
        }
        Log.e(this.javaClass.simpleName, "doWork(): response=$response")
        val outputData: Data = workDataOf(WorkManagerHelper.TAG_WORKER_DOWNLOAD_OUTPUT to response)
        Log.e(
            this.javaClass.simpleName, "doWork(): outputData=${
                outputData.getString(
                    WorkManagerHelper.TAG_WORKER_DOWNLOAD_OUTPUT
                )
            }"
        )
        return Result.success(outputData)
    }

    private fun downloadWorkerSyncHttpCall(urlStr: String): String? {
        //Create OkHttp client
        val client = OkHttpClient()
        //Build request
        val request = Request.Builder()
            .url(urlStr)
            .get()
            .build()
        //Execute hppt request call
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        Log.e(
            this.javaClass.simpleName,
            "downloadSynchronously(): response.request=" + response.request
        )
        Log.e(
            this.javaClass.simpleName,
            "downloadSynchronously(): response.message=" + response.message
        )
        Log.e(this.javaClass.simpleName, "downloadSynchronously(): response.body=$responseBody")
        return responseBody
    }
}