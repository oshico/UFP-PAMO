package edu.ufp.pam.examples.p05_farrusco

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import edu.ufp.pam.examples.R
import edu.ufp.pam.examples.p05_farrusco.viewmodel.FarruscoViewModel
import edu.ufp.pam.examples.p05_farrusco.volley.SingletonVolleyRequestQueue
import edu.ufp.pam.examples.p05_farrusco.workers.WorkManagerHelper

class MainFarruscoEmptyActivity : AppCompatActivity() {


    /** ToDo: define an Enumeration of HTTP async execution methods */
    private enum class HttpAsyncMethod {
        VIEWMODEL, VOLLEY, WORKMANAGER
    }

    /** ToDo: choose the HTTP async execution method to use by uncommenting the selected line */
    private val httpAsyncMethodToUse: HttpAsyncMethod = HttpAsyncMethod.VIEWMODEL;
    //private val httpAsyncMethodToUse : HttpAsyncMethod = HttpAsyncMethod.VOLLEY;
    //private val httpAsyncMethodToUse : HttpAsyncMethod = HttpAsyncMethod.WORKMANAGER;

    /** ToDo: create the ViewModel to execute async http calls */
    private lateinit var farruscoViewModel: FarruscoViewModel

    /** ToDo: create the Volley RequestQueue to execute async http calls */
    private lateinit var volleyRequestQueue: RequestQueue

    /** ToDo: create the WorkManager to execute async http calls */
    private lateinit var workManagerHelper: WorkManagerHelper

    /** ToDo: create the Helper class to execute async net calls */
    private lateinit var farruscoHelper: FarruscoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_farrusco_empty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // =====================================================================================
        // ToDo: setup the views components defined in the robot control layout
        // =====================================================================================

        val textViewReply = findViewById<TextView>(R.id.textViewReply)

        //ToDo: choose which of 3 async methods to use to make http calls (similar to switch-case)
        when (httpAsyncMethodToUse) {
            HttpAsyncMethod.VIEWMODEL -> {
                //ToDo: =================== Setup ViewModel to make async HTTP Request ===================
                Log.d(this.javaClass.simpleName, "onCreate(): going to set VIEWMODEL context...")

                this.farruscoViewModel =
                    ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
                    ).get(FarruscoViewModel::class.java)


                farruscoHelper =
                    FarruscoHelper(
                        this,
                        farruscoViewModel,
                        null,
                        null,
                        textViewReply
                    )
            }

            HttpAsyncMethod.VOLLEY -> {
                //ToDo: =================== Setup Volley to make async HTTP Request ===================
                Log.e(this.javaClass.simpleName, "onCreate(): going to set VOLLEY context...")
                this.volleyRequestQueue =
                    SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue


                farruscoHelper =
                    FarruscoHelper(
                        this,
                        null,
                        null,
                        volleyRequestQueue,
                        textViewReply
                    )
            }

            HttpAsyncMethod.WORKMANAGER -> {
                //ToDo: =================== Setup WorkManager to make async HTTP Request ===================
                Log.e(this.javaClass.simpleName, "onCreate(): going to set WORKMANAGER context...")

                this.workManagerHelper = WorkManagerHelper(this.application)


                farruscoHelper =
                    FarruscoHelper(
                        this,
                        null,
                        workManagerHelper,
                        null,
                        textViewReply
                    )
            }
        }

        //ToDo: get buttonDownloadTestFile to trigger one of 3 ways of async executing http call
        val buttonDownloadTestFile = findViewById<Button>(R.id.buttonDownloadTestFile)
        //ToDo: associate the button handler for download
        buttonDownloadTestFile.setOnClickListener { view ->

            //ToDo: get view to obtain url to download data.txt file from HTTP server
            //  (e.g. http://homepage.ufp.pt/rmoreira/LP2/data.txt)
            val editTextUrl = findViewById<TextView>(R.id.editTextUrl)
            val testUrlStr = editTextUrl.text.toString() //http://homepage.ufp.pt/
            val testQueryStr = "/rmoreira/LP2/data.txt"

            //ToDo: build URL to control the robot
            //  (e.g. http://homepage.ufp.pt/sendToFarrusco?i=1&t=1000&m=12)
            // where
            //  'i' is the id of the robot: i=1 | i=2
            //  't' is the time of movement in ms: t=1000
            //  'm' is the movement: m=12 | m=6 | m=3 | m=9

            /** ToDo: choose which of 3 async methods to use to make http calls */
            when (httpAsyncMethodToUse) {
                HttpAsyncMethod.VIEWMODEL -> {
                    farruscoHelper.launchViewModelAsyncHttpRequest(testUrlStr, testQueryStr)
                }

                HttpAsyncMethod.VOLLEY -> {
                    farruscoHelper.launchVolleyAsyncHttpRequest(testUrlStr, testQueryStr)
                }

                HttpAsyncMethod.WORKMANAGER -> {
                    farruscoHelper.launchWorkerAsyncHttpRequest(testUrlStr, testQueryStr)
                }
            }
        }
    } //end onCreate()


    /**
     * =========================================================================
     *  ToDo: Challenge: control robot Farrusco... :)
     *   - Create new layout activity_main_farrusco_empty_robotcontrol.xml;
     *   - On onCreate() load instead activity_main_farrusco_empty_robotcontrol.xml;
     *   - Create function robotControl() to set image button handlers able to control
     *      Farrusco robot movements.
     * ==========================================================================
     */
//    private fun robotControl(){
//        // ToDO: set farrusco control buttons to execute http calls
//        //Use a listener for UP button
//        val imageButtonUp = findViewById<ImageButton>(R.id.imageButtonUp)
//        imageButtonUp.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                //url + "?i=" + farruscoID + "&m=12&t=" + time
//                setFarruscoParametersAndLaunchAsyncCall("12")
//            }
//        })
//        //Use lambda function for RIGHT button
//        val imageButtonRight = findViewById<ImageButton>(R.id.imageButtonRight)
//        imageButtonRight.setOnClickListener {
//            //url + "?i=" + farruscoID + "&m=3&t=" + time
//            setFarruscoParametersAndLaunchAsyncCall("3")
//        }
//        //Use a listener for LEFT button
//        val imageButtonLeft = findViewById<ImageButton>(R.id.imageButtonLeft)
//        imageButtonLeft.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                //url + "?i=" + farruscoID + "&m=9&t=" + time
//                setFarruscoParametersAndLaunchAsyncCall("9")
//            }
//        })
//        //Use lambda function for DOWN button
//        val imageButtonDown = findViewById<ImageButton>(R.id.imageButtonDown)
//        imageButtonDown.setOnClickListener {
//            //url + "?i=" + farruscoID + "&m=6&t=" + time
//            //fun setFarruscoParametersAndLaunch(urlStr: String, idStr: String, moveStr: String, timeStr: String) {
//            setFarruscoParametersAndLaunchAsyncCall("6")
//        }
//    } //End robotControl()

    /**
     * ToDo: create Façade function that:
     *  1. Receives type of movement (12, 6, 9, 3) from clicked button
     *  2. Retrieves data from GUI components (Views) and
     *  3. Launches http call using the FarruscoHelper inner class.
     */
    private fun setFarruscoParametersAndLaunchAsyncCall(move: String) {
        // ToDo: get URL where HTTP server (gateway) to farrusco is listenning
        val editTextUrl = findViewById<EditText>(R.id.editTextUrl)
        val urlStr = editTextUrl.text.toString()
        //ToDo: get Time of movement
        val editTextTime = findViewById<EditText>(R.id.editTextTime)
        val timeStr = editTextTime.text.toString()
        //ToDo: get Farrusco ID (2)
        val editTextID = findViewById<EditText>(R.id.editTextID)
        val idStr = editTextID.text.toString()
        //ToDo: assemble query string, e.g., ?i=2&m=12&t=500 (ID=2, MOVE=FW, TIME=500ms)
        val queryStr = "?i=$idStr&m=$move&t=$timeStr"
        Log.i(
            this.javaClass.simpleName,
            "setFarruscoParametersAndLaunchAsyncCall(): urlStr=$urlStr"
        )
        Log.i(
            this.javaClass.simpleName,
            "setFarruscoParametersAndLaunchAsyncCall(): queryStr=$queryStr"
        )

        /** ToDo: choose which of 3 async methods to use to make http calls */
        when (httpAsyncMethodToUse) {
            HttpAsyncMethod.VOLLEY -> {

                farruscoHelper.launchVolleyAsyncHttpRequest(urlStr, queryStr)
            }

            HttpAsyncMethod.WORKMANAGER -> {

                farruscoHelper.launchWorkerAsyncHttpRequest(urlStr, queryStr)
            }

            else -> {
                Log.d(
                    this.javaClass.simpleName,
                    "setFarruscoParametersAndLaunchAsyncCall(): Use httpAsyncMethodToUse = VOLLEY or WORKMANAGER"
                )
            }
        }
    }// setFarruscoParametersAndLaunchAsyncCall()

    /**
     * ToDO: use onStop() to cancel all Activity pending requests from Volley.
     */
    protected override fun onStop() {
        super.onStop()

        val volleyRequestQueue =
            SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        volleyRequestQueue.cancelAll(this.farruscoHelper.TAG_TO_CANCEL_HTTP_REQUEST)
    }//End onStop()

    /**
     * ToDo:
     *   create Helper class to make http calls (cf. communicate with Farrusco robot via HTTP).
     *   This class receives:
     *      - the Activity context
     *      - the ViewModel to make async http calls
     *      - the WorkManagerHelper to make async http calls
     *      - the Volley RequestQueue to make async http calls
     *      - the TextView to display the http reply
     */
    class FarruscoHelper(

        private val mainFarruscoEmptyActivity: MainFarruscoEmptyActivity,

        private val farruscoViewModel: FarruscoViewModel?,

        private val workManagerHelper: WorkManagerHelper?,

        private val volleyRequestQueue: RequestQueue?,

        private val textViewReply: TextView
    ) {
        /** ToDo: define a key/tag to cancel pending http requests from Volley */
        val TAG_TO_CANCEL_HTTP_REQUEST = "TAG_TO_CANCEL_HTTP_REQUEST"

        /**
         * ToDo: Use the ViewModel to async execute HTTP Call.
         */
        fun launchViewModelAsyncHttpRequest(urlStr: String, queryStr: String) {
            val url = "$urlStr$queryStr"
            Log.d(this.javaClass.simpleName, "launchViewModelAsyncHttpRequest(): url=$url")


            farruscoViewModel?.getHttpReply()?.observe(
                mainFarruscoEmptyActivity,
                Observer { httpReply ->

                    httpReply?.let {
                        val reply = farruscoViewModel.getHttpReply().value
                        Log.d(this.javaClass.simpleName, "onChanged(): reply = $reply")
                        //Put file content into textview
                        //val textViewReply = findViewById<TextView>(R.id.textViewReply)
                        //textViewReply.text = reply
                        textViewReply.text = "ViewModel Observer Response is:\n$reply"
                    }
                }
            )
            farruscoViewModel?.setFileURI(url)
            farruscoViewModel?.launchAsyncOkHttpCall()
        }

        /**
         * ToDo: Use WorkManager to enqueue a DownloadWorker to async execute HTTP Call.
         */
        fun launchWorkerAsyncHttpRequest(urlStr: String, queryStr: String) {
            val url = "$urlStr$queryStr"
            Log.e(this.javaClass.simpleName, "launchWorkerAsyncHttpRequest(): url=$url")

            workManagerHelper?.worManagerPruneWork()

            workManagerHelper?.getOutputWorkInfos()?.observe(
                mainFarruscoEmptyActivity,
                Observer {
                    Log.d(this.javaClass.simpleName, "observer(): it.size = ${it.size}")
                    val size = it.size - 1
                    for (i in 0..size) {
                        var workInfo = it.get(i)
                        Log.d(
                            this.javaClass.simpleName,
                            "observer(): workInfo.state = ${workInfo.state}"
                        )
                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            Log.e(
                                this.javaClass.simpleName,
                                "observer(): workInfo[$i].tags = ${workInfo.tags}"
                            )
                            Log.e(
                                this.javaClass.simpleName,
                                "observer(): workInfo[$i].id = ${workInfo.id}"
                            )
                            Log.e(
                                this.javaClass.simpleName,
                                "observer(): workInfo[$i].progress = ${workInfo.progress}"
                            )
                            val response =
                                workInfo.outputData.getString(WorkManagerHelper.TAG_WORKER_DOWNLOAD_OUTPUT)
                            Log.e(
                                this.javaClass.simpleName,
                                "observer(): workInfo[$i].outputData response = ${response}"
                            )
                            if (i == size) {
                                textViewReply.text = "DownloadWorker Response is:\n$response"
                            }
                        }
                    }
                }
            )
            workManagerHelper?.setFileURI("$urlStr$queryStr")
            workManagerHelper?.launchDownloadWorker()
        }

        /**
         * ToDo: Use Volley to async execute HTTP Call.
         *
         * Volley uses an async task but delivers parsed responses on the main thread,
         * which may be convenient for populating UI controls with received data.
         * However, this may be critical to many important semantics provided by the library,
         * particularly related to canceling requests... done on Activity onStop() method.
         *
         * Please check also the Cronet Library, which provides Chromium network stack to perform
         * network operations in Android apps... using gradle dependency:
         *   implementation 'com.google.android.gms:play-services-cronet:16.0.0'
         */
        fun launchVolleyAsyncHttpRequest(urlStr: String, queryStr: String) {
            val url = "$urlStr$queryStr"
            Log.e(this.javaClass.simpleName, "launchVolleyAsyncHttpRequest(): url=$url")

            // Get the RequestQueue (singleton) - already done previously!
            //val queue = Volley.newRequestQueue(this)
            //val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue

            // Create Request with 2 Listeners:
            //  1 listener to handle Response from the provided URL;
            //  1 listener to handle error Response.
            val stringRequest = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    Log.d(
                        this.javaClass.simpleName,
                        "launchVolleyAsyncHttpRequest(): Response.Listener Response=${response}"
                    )
                    textViewReply.text = "Volley Response is:\n$response"
                },
                { //Handle Error
                        error ->
                    Log.d(
                        this.javaClass.simpleName,
                        "launchVolleyAsyncHttpRequest(): Response.Listener Error=$error"
                    )
                    textViewReply.text = "Volley Download Handle Error!!!!"
                }
            )
            stringRequest.tag = TAG_TO_CANCEL_HTTP_REQUEST
            volleyRequestQueue?.add(stringRequest)
        }
    }
}