package edu.ufp.pam.examples.p06_someservices.messengerservice


import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import edu.ufp.pam.examples.p05_farrusco.volley.SingletonVolleyRequestQueue


/** Command for service to display a message  */
public const val MSG_TO_SERVICE_SAY_HELLO = 1
public const val MSG_TO_CLIENT_REPLY_OK = 2
public const val MSG_TO_SERVICE_DOWNLOAD_FILE = 3
public const val MSG_TO_CLIENT_REPLY_FILE_CONTENT = 4


class MessengerService : Service() {

    /** Target published for clients to send messages to IncomingHandler. */
    private lateinit var serviceMessenger: Messenger
    /** Target published for service to send messages to client. */
    private lateinit var clientMessenger: Messenger

    /** Handler for incoming messages from clients via Messenger. */
    //internal class IncomingHandler(context: Context) : Handler() {
    inner class IncomingHandler() : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(
                this.javaClass.simpleName,
                "handleMessage(): service side msg.obj=${msg.obj.toString()}"
            )
            //Set reply messenger to client
            clientMessenger = msg.replyTo
            when (msg.what) {
                MSG_TO_SERVICE_SAY_HELLO -> {
                    val helloMsg = msg.obj.toString()
                    //Toast.makeText(applicationContext, "Service got msg = $helloMsg", Toast.LENGTH_SHORT).show()
                    Log.i(
                        this.javaClass.simpleName,
                        "handleMessage(): received on service hello msg.obj=${helloMsg}"
                    )
                    val msgToClient: Message =
                        Message.obtain(null, MSG_TO_CLIENT_REPLY_OK, "Hello from Service!")
                    msg.replyTo.send(msgToClient)
                }
                MSG_TO_SERVICE_DOWNLOAD_FILE -> {
                    val url: String = msg.obj.toString()
                    //Toast.makeText(applicationContext, "Service got msg = $url", Toast.LENGTH_LONG).show()
                    Log.i(
                        this.javaClass.simpleName,
                        "handleMessage(): received on service download msg.obg=${url}"
                    )

                    //Using sync call will generate an Exception since service runs in main thread
                    /*
                    val responseBody = runHttpGetCallWithOkHttp(url)
                    val msgToClient: Message = Message.obtain(null, MSG_TO_CLIENT_REPLY_FILE_CONTENT, responseBody)
                    msg.replyTo.send(msgToClient)
                    */

                    //Using async call will run on a separate thread
                    launchAsyncVolleyHttpRequest(url)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        //TODO("Return the communication channel to the service.")
        Toast.makeText(applicationContext, "binding...", Toast.LENGTH_SHORT).show()
        //Create the service Messenger
        serviceMessenger = Messenger(IncomingHandler())
        return serviceMessenger.binder
    }

    /**
     * Helper method for async download...
     * use the existing SingletonVolleyRequestQueue from p05_farrusco.
     * */
    private fun launchAsyncVolleyHttpRequest(urlStr: String) {
        // Get the RequestQueue (singleton)
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        //  1 listener to handle Response from the provided URL;
        //  1 listener for error handling.
        val stringRequest = StringRequest(
            Request.Method.GET, urlStr,
            { //Handle Response
                    response ->
                Log.i(this.javaClass.simpleName,
                    "launchAsyncVolleyHttpRequest(): Response.Listener Response=${response}")
                sendMsgBackToClientMessenger(response)
            },
            { //Handle Error
                    error ->
                Log.i(this.javaClass.simpleName,
                    "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
                sendMsgBackToClientMessenger("Download ERROR!! ${error.message}")
            }
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun sendMsgBackToClientMessenger(reply: String){
        val msgToClient: Message = Message.obtain(null, MSG_TO_CLIENT_REPLY_FILE_CONTENT, reply)
        if(clientMessenger != null) {
            clientMessenger.send(msgToClient)
        }
    }
}
