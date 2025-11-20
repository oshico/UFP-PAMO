package edu.ufp.pam.examples.p05_farrusco.workers

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest

class WorkManagerHelper(app: Application) {

    internal var fileURI: Uri? = null
    private val workManager = WorkManager.getInstance(app)

    //Create LiveData for async observing HTTP response
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        // Init block: Whenever current Worker changes WorkInfo we live listen to changes.
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_WORKER_DOWNLOAD_OUTPUT)
    }

    fun getOutputWorkInfos(): LiveData<List<WorkInfo>> {
        return outputWorkInfos
    }

    fun worManagerPruneWork() {
        workManager.pruneWork()
    }

    /*
     * Define static constants to be used has Keys/Tags of input/output data parameters
     * to be passed or to be received to/from Workers or WorkRequests.
     */
    companion object {
        const val KEY_WORKER_DOWNLOAD_INPUT_URL =
            "edu.ufp.pam.examples.farrusco.KEY_WORKER_DOWNLOAD_INPUT_URL"
        const val TAG_WORKER_DOWNLOAD_OUTPUT =
            "edu.ufp.pam.examples.farrusco.TAG_WORKER_DOWNLOAD_OUTPUT"
    }

    internal fun setFileURI(uri: String?) {
        fileURI = uriOrNull(uri)
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    /**
     * Create input data bundle which includes the URI to operate on
     * @return Data which contains the Uri as String
     */
    private fun createInputDataForUri(): Data {
        //Create Data object
        val builder = Data.Builder()
        fileURI?.let {
            builder.putString(KEY_WORKER_DOWNLOAD_INPUT_URL, fileURI.toString())
        }
        return builder.build()
    }

    /**
     * Create DownloadWorker to sync execute WorkRequest.
     */
    public fun launchDownloadWorker() {
        Log.e(
            this.javaClass.simpleName,
            "launchDownloadWorker(): going to enqueue DownloadWorker to GET http file..."
        )
        //Create a OneTimeWorkRequest with some InputData & associated Tag
        val downloadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(createInputDataForUri())
                .addTag(TAG_WORKER_DOWNLOAD_OUTPUT)
                .build()

        //Enqueue WorkRequest to be executed asynchronously
        val enqueue: Operation = workManager.enqueue(downloadWorkRequest)
        Log.e(
            this.javaClass.simpleName,
            "launchDownloadWorker(): enqueue.result = ${enqueue.result.get()}"
        )
    }
}