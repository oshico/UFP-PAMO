package edu.ufp.pam.examples.p06_someservices.jobintentservice


import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import androidx.core.app.JobIntentService
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.OkHttpClient
import okhttp3.Request


/** ToDo: Define custom Intent actions */
const val BROADCAST_DOWNLOAD_STATUS_ACTION =
    "edu.ufp.pam.examples.someservices.BROADCAST_DOWNLOAD_STATUS"
const val BROADCAST_ZOOM_IMAGE_STATUS_ACTION =
    "edu.ufp.pam.examples.someservices.BROADCAST_ZOOM_IMAGE_STATUS"

/** ToDo: Define the key for the status "extra" in the Intent BROADCAST_DOWNLOAD_STATUS_ACTION */
const val EXTENDED_DATA_STATUS =
    "edu.ufp.pam.examples.someservices.STATUS"


// TODO: Rename actions by choosing action names describing tasks
//  that this IntentService can perform, e.g. ACTION_FOO -> ACTION_DOWNLOAD_FILE
private const val ACTION_DOWNLOAD_FILE =
    "edu.ufp.pam.examples.someservices.action.DOWNLOAD_FILE"
private const val ACTION_DO_SOMETHING_ELSE_BAZ =
    "edu.ufp.pam.examples.someservices.action.BAZ"

// TODO: Rename parameters e.g. EXTRA_PARAM1 -> EXTRA_PARAM1_URL
private const val EXTRA_PARAM1_URL =
    "edu.ufp.pam.examples.someservices.extra.PARAM1_URL"
private const val EXTRA_PARAM2_FILENAME =
    "edu.ufp.pam.examples.someservices.extra.PARAM2_FILENAME"


/**
 * An [IntentService] subclass handles asynchronous task requests in
 * a service running on a separate handler thread.
 * TODO: Customize class by updating intent actions, extra parameters and static helper methods.
 */
class DownloadJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        when (intent?.action) {
            ACTION_DOWNLOAD_FILE -> {
                val param1Url = intent.getStringExtra(EXTRA_PARAM1_URL)
                val param2Filename = intent.getStringExtra(EXTRA_PARAM2_FILENAME)
                if (param1Url != null && param2Filename != null) {
                    handleActionDownloadFile(param1Url, param2Filename)
                }
            }

            ACTION_DO_SOMETHING_ELSE_BAZ -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1_URL)
                val param2 = intent.getStringExtra(EXTRA_PARAM2_FILENAME)
                if (param1 != null && param2 != null) {
                    handleActionBaz(param1, param2)
                }
            }
        }
    }

    /**
     * Handle action DownloadFile in the provided background thread with given parameters.
     */
    private fun handleActionDownloadFile(param1Url: String, param2Filename: String) {
        // Construct full resource URL
        val resource = "${param1Url}/${param2Filename}"
        Log.e(this.javaClass.simpleName, "handleActionDownloadFile(): resource=$resource")
        val responseBody = runHttpGetCallWithOkHttp(resource)
        // Post status to LiveData observable to notify observers (e.g., Activity/Fragment)
        DownloadStatusLiveData.postStatus(responseBody ?: "Download Failed")
    }

    /** Method for actually executing HTTP get request */
    private fun runHttpGetCallWithOkHttp(urlStr: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlStr)
            .get()
            .build()
        //Execute sync request and get response
        val response = client.newCall(request).execute()
        Log.i(this.javaClass.simpleName, "runHttpGetCallWithOkHttp(): response=" + response.request)
        Log.i(this.javaClass.simpleName, "runHttpGetCallWithOkHttp(): message=" + response.message)
        //Get response body as string
        val responseBody = response.body.string()
        Log.i(this.javaClass.simpleName, "runHttpGetCallWithOkHttp(): body=$responseBody")
        return responseBody
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String?, param2: String?) {
        TODO("Handle action Baz")
    }

    /**
     * Companion object provides helper methods for calling this service.
     * For example, the Activity calls enqueueWork() to start the job intent service.
     */
    companion object {

        // A unique job ID is required for JobIntentService.
        private const val DOWNLOAD_JOB_ID = 1001
        private const val BAZ_JOB_ID = 1002

        /**
         * Enqueues work for this service to download a file.
         * If the service is already performing a task this action will be queued.
         */
        @JvmStatic
        fun enqueueWorkDownloadFile(context: Context, param1Url: String, param2Filename: String) {
            val intent = Intent(context, DownloadJobIntentService::class.java).apply {
                action = ACTION_DOWNLOAD_FILE
                putExtra(EXTRA_PARAM1_URL, param1Url)
                putExtra(EXTRA_PARAM2_FILENAME, param2Filename)
            }
            JobIntentService.enqueueWork(context, DownloadJobIntentService::class.java, DOWNLOAD_JOB_ID, intent)
        }

        /**
         * Enqueues work for this service to download a file.
         * If the service is already performing a task this action will be queued.
         */
        @JvmStatic
        fun enqueueWorkDoSomethingElseBaz(context: Context, param1Url: String, param2Filename: String) {
            val intent = Intent(context, DownloadJobIntentService::class.java).apply {
                action = ACTION_DO_SOMETHING_ELSE_BAZ
                putExtra(EXTRA_PARAM1_URL, param1Url)
                putExtra(EXTRA_PARAM2_FILENAME, param2Filename)
            }
            JobIntentService.enqueueWork(context, DownloadJobIntentService::class.java, BAZ_JOB_ID, intent)
        }
    }
}