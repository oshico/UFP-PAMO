package edu.ufp.pam.examples.p06_someservices.jobintentservice

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import edu.ufp.pam.examples.R

class MainDownloadJobIntentServiceEmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_download_job_intent_service_empty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get reference to Button to start download
        val buttonDownload = findViewById<Button>(R.id.buttonDownload)
        // Set onClick listener
        buttonDownload.setOnClickListener {
            /* Call IntentService to download file */
            val editTextUrl = findViewById<EditText>(R.id.editTextUrl)
            val url: String = editTextUrl.text.toString()

            val editTextFilename = findViewById<EditText>(R.id.editTextFilename)
            val filename: String = editTextFilename.text.toString()
            Log.e(this.javaClass.simpleName, "onClick(): url=${url}/${filename}")
            //Start the service to perform a *Foo* action with given parameters...
            DownloadJobIntentService.enqueueWorkDownloadFile(this, url, filename)
            // Alternatively, start download using WorkManager
            //this.startDownloadWithWorkManager(url, filename)
        }

        // Get reference to EditText to show downloaded file content after download
        val editTextFileContent = findViewById<EditText>(R.id.editTextFileContent)
        // Observe the LiveData from the singleton updated by the JobIntentService
        DownloadStatusLiveData.status.observe(this) { status ->
            // This method runs every time the status is updated by the intent service.
            // Update TextView UI element here...
            Log.e(this.javaClass.simpleName, "observer(): observed status=${status}")
            editTextFileContent.setText(status)
        }
    }//End onCreate()

    /**
     * Method used to start a download using WorkManager
     * */
    private fun startDownloadWithWorkManager(url: String, filename: String) {
        // 1. Get an instance of WorkManager
        val workManager = WorkManager.getInstance(applicationContext)

        // 2. Create input data to pass to the Worker
        val inputData = Data.Builder()
            .putString(DownloadCoroutineWorker.KEY_INPUT_URL, url)
            .putString(DownloadCoroutineWorker.KEY_INPUT_FILENAME, filename)
            .build()

        // 3. Create a OneTimeWorkRequest
        val downloadWorkRequest = OneTimeWorkRequest.Builder(DownloadCoroutineWorker::class.java)
            .setInputData(inputData)
            // Add constraints here if needed, e.g. use only WiFi connection (NetworkType.UNMETERED)
            // .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
            .build()

        // 4. Enqueue the work
        workManager.enqueue(downloadWorkRequest)

        // 5. Observe the work's status (Optional but Recommended)
        workManager.getWorkInfoByIdLiveData(downloadWorkRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    val status = workInfo.outputData.getString(DownloadCoroutineWorker.KEY_OUTPUT_STATUS)
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            // Work finished successfully, get the output data
                            Log.d(this.javaClass.simpleName, "WorkManager download, Work SUCCEEDED with status: $status")
                            val editTextFileContent = findViewById<EditText>(R.id.editTextFileContent)
                            editTextFileContent.setText(status)
                        }
                        WorkInfo.State.FAILED -> {
                            Log.e(this.javaClass.simpleName, "WorkManager download, Work FAILED with status: $status")
                            val editTextFileContent = findViewById<EditText>(R.id.editTextFileContent)
                            editTextFileContent.setText("WorkManager download, Work FAILED with status: $status")
                        }
                        WorkInfo.State.RUNNING -> {
                            Log.d(this.javaClass.simpleName, "WorkManager download, Work RUNNING with status: $status")
                            val editTextFileContent = findViewById<EditText>(R.id.editTextFileContent)
                            editTextFileContent.setText("WorkManager download, Work RUNNING with status: $status")
                        }
                        else -> {
                            Log.d(this.javaClass.simpleName, "WorkManager download, Work status: ${workInfo.state}")
                        }
                    }
                } else {
                    Log.d(this.javaClass.simpleName, "WorkManager, workInfo is null!!!")
                }
            }
    } //End startDownloadWithWorkManager()
}