package edu.ufp.pam.examples.p06_someservices.jobintentservice

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import okhttp3.OkHttpClient
import okhttp3.Request

class DownloadCoroutineWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    // Define keys for input and output data
    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_INPUT_FILENAME = "KEY_INPUT_FILENAME"
        const val KEY_OUTPUT_STATUS = "KEY_OUTPUT_STATUS"
    }

    /**
     * Method that runs in the background thread to perform the work.
     * CoroutineWorker already provides a background thread.
     */
    override suspend fun doWork(): Result {
        // 1. Get input data from the WorkRequest
        val url = inputData.getString(KEY_INPUT_URL) ?: return Result.failure()
        val filename = inputData.getString(KEY_INPUT_FILENAME) ?: return Result.failure()

        val resource = "$url/$filename"
        Log.d("DownloadWorker", "Starting download for: $resource")

        return try {
            // 2. Perform the download logic with OkHttp
            val client = OkHttpClient()
            val request = Request.Builder().url(resource).get().build()
            val response = client.newCall(request).execute()
            val responseBody = response.body.string()

            // 3. Create output data and return success
            val outputData = workDataOf(KEY_OUTPUT_STATUS to responseBody)
            Log.d(this.javaClass.simpleName, "Download successful outputData = $outputData")
            // Return the output data
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Download failed!!", e)
            // 4. Return failure if an exception occurs
            Result.failure()
        }
    }
}
