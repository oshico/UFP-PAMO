package edu.ufp.pam.examples.p06_someservices.messengerservice


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.ufp.pam.examples.R


/**
 * The Activity send Messages to the MessengerService and receives reply Messages from it.
 * We need two Messenger objects, one Messenger for Activity to send messages to the service and
 * another Messenger for service to send messages back to the Activity.
 *
 * When client Activity sends message to service, it includes the client's Messenger in the
 * replyTo parameter of the send() method. This client Messenger allows to receive reply on the
 * onServiceConnected() callback.
 * */
class MainMessengerServiceEmptyActivity : AppCompatActivity() {

    /** ToDo: declare Messenger used by Activity client to send/communicate with service. */
    private var serviceMessenger: Messenger? = null
    /** ToDo: declare flag indicating whether we have called bind on the service.  */
    private var serviceBound: Boolean = false
    /** ToDo: declare Messenger used by service to send/communicate back with Activity client. */
    private var clientMessenger: Messenger? = null

    /** ToDo: create ServiceConnection object used to instantiate local Activity Messenger
     *   for connecting with the IBinder of service. */
    private val serviceConnection = object : ServiceConnection {
        /** Method called when the connection with the service has been established, providing an
        object (IBinder) used to interact with the service. */
        override fun onServiceConnected(className: ComponentName, serviceBinder: IBinder) {
            // Client communicates with the service using a Messenger object, i.e. a client-side
            // representation of the raw service IBinder object.
            serviceMessenger = Messenger(serviceBinder)
            serviceBound = true
        }

        /** Method called when the connection with the service has been unexpectedly disconnected
         * (i.e. its process crashed). */
        override fun onServiceDisconnected(className: ComponentName) {
            serviceMessenger = null
            serviceBound = false
        }
    }

    /** ToDo: create Handler for receiving incoming messages from service via a Messenger. */
    internal class IncomingHandler(context: Context,
                                   val editTextDataOutput : EditText,
                                   private val applicationContext: Context = context.applicationContext
    ) : Handler() {
        //Client-side handler callback which receives reply from service
        override fun handleMessage(msg: Message) {
            Log.e(this.javaClass.simpleName, "handleMessage(): client side msg.obj=${msg.obj}")
            when (msg.what) {
                MSG_TO_CLIENT_REPLY_OK -> {
                    val reply = msg.obj.toString()
                    Toast.makeText(applicationContext, "Client got msg: ${reply}", Toast.LENGTH_SHORT).show()
                    editTextDataOutput.setText(reply)
                }
                MSG_TO_CLIENT_REPLY_FILE_CONTENT -> {
                    val reply = msg.obj.toString()
                    Toast.makeText(applicationContext, "Client got downloaded: $reply", Toast.LENGTH_LONG).show()
                    editTextDataOutput.setText(reply)
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * ToDo: function called when buttonSayHello clicked to send a message to the service
     * */
    private fun sayHelloToService(v: View) {
        Log.i(this.javaClass.simpleName,"sayHelloToService(): button pressed serviceBound=${serviceBound}")
        if (!serviceBound) return
        // Create and send a message to the service, using a supported 'what' value
        try {
            //Prepare a message to be sent to service
            //val msg: Message = Message.obtain(null, MSG_TO_SERVICE_SEND_HELLO, 0, 0)
            val msg: Message = Message.obtain(null, MSG_TO_SERVICE_SAY_HELLO, "Hello from client!")
            val url = "http://homepage.ufp.pt/rmoreira/LP2/data.txt"
            //val msg: Message = Message.obtain(null, MSG_TO_SERVICE_DOWNLOAD_FILE, url)
            //Add also to msg the client Messenger so that service may invoke callback
            msg.replyTo = clientMessenger
            serviceMessenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * onCreate() - initialize Activity client Messenger and button listener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_messenger_service_empty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Create client Messenger
        if (this.clientMessenger==null){
            val editTextDataOutput = findViewById<EditText>(R.id.editTextDataOutput)
            // Create client Messenger (with Handler) to receive messages from service
            clientMessenger = Messenger(IncomingHandler(this, editTextDataOutput))
        }

        //Create button listener to call service
        val buttonSayHello = findViewById<Button>(R.id.buttonSayHello)
        buttonSayHello.setOnClickListener {
            sayHelloToService(it)
        }
    }//End onCreate()


    /** ToDO: Bind during onStart() assures interaction with service only while activity is visible */
    override fun onStart() {
        super.onStart()
        // Use an Intent to bind to the service, i.e. call bindService():
        // binding is asynchronous, hence bindService() returns immediately without returning the
        // IBinder to the client.
        // The client must create an instance of ServiceConnection and pass it to bindService()
        // to receive the IBinder (ServiceConnection includes a callback method to deliver the
        // IBinder back to client).
        Intent(this, MessengerService::class.java).also {
                intent -> bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /** ToDo: Unbind during onStop() assures interaction with service stops while activity is not visible */
    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
    }
}