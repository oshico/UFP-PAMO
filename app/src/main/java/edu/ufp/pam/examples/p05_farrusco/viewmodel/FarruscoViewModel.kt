package edu.ufp.pam.examples.p05_farrusco.viewmodel


import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class FarruscoViewModel(app: Application) : AndroidViewModel(app) {

    internal var fileURI: Uri? = null

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

    private val httpReply: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            //launchAsyncOkHttpCall()
        }
    }

    fun getHttpReply(): LiveData<String> {
        return httpReply
    }

    /**
     * Managing async execution of OkHttp call through viewModelScope.launch(Dispatchers.IO){...}
     */
    public fun launchAsyncOkHttpCall() {
        Log.d(
            this.javaClass.simpleName,
            "launchAsyncOkHttpCall(): going to async exe OkHttp Request URI =$fileURI"
        )
        // Do an asynchronous operation to fetch file:
        // Execution of AsynTask is deprecated => use viewModelScope.launch() instead.
        viewModelScope.launch(Dispatchers.IO) {
            //Create an OkHttpClient object
            val client = OkHttpClient()
            //Build GET HttpRequest
            val request = Request.Builder()
                .url(fileURI.toString())
                .get()
                .build()
            //Execute the HttpRequest
            val response = client.newCall(request).execute()

            //Print details of HttpResponse
            val responseBody = response.body?.string()
            Log.d(
                this.javaClass.simpleName,
                "viewModelScope.launch(): response.request=" + response.request
            )
            Log.d(
                this.javaClass.simpleName,
                "viewModelScope.launch(): response.message=" + response.message
            )
            Log.d(this.javaClass.simpleName, "viewModelScope.launch(): response.body=$responseBody")

            //VERY IMPORTANT: Update LiveData value with Http response
            httpReply.postValue(responseBody)
        }
    }


}

