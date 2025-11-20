package edu.ufp.pam.examples.p06_someservices.jobintentservice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * A Singleton object that holds a LiveData instance that can be observed by the UI
 * and updated by the service (replace LocalBroadcastManager for download status).
 */
object DownloadStatusLiveData {
    // Private MutableLiveData that can only be changed from within this object.
    private val downloadStatus = MutableLiveData<String>()

    // Public non-mutable LiveData that the UI can safely observe.
    val status: LiveData<String> = downloadStatus

    // Method for the service to post a new status update.
    // Use postValue() as the service is on a background thread.
    fun postStatus(newStatus: String) {
        downloadStatus.postValue(newStatus)
    }
}

