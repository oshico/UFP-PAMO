package edu.ufp.pam.examples.p06_someservices.bindservice


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.ufp.pam.examples.R

class MainRandomBindLocalServiceEmptyActivity : AppCompatActivity() {


    //  ToDo: declare service binding variables
    private lateinit var mService: RandomBindLocalService
    private var mBound: Boolean = false

    /** ToDO: define callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(this.javaClass.simpleName, "onServiceConnected(): going get RandomBindLocalService...")
            //Bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as RandomBindLocalService.RandomLocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_random_bind_local_service_empty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /** ToDo: bind to service onStart  */
    override fun onStart() {
        super.onStart()
        Log.i(this.javaClass.simpleName, "onStart(): going to bindService()...")
        // Bind to LocalService through an Intent
        Intent(this, RandomBindLocalService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    /** ToDO: clients should unbind from services when connection is no longer needed */
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    /**
     * ToDo: method called when buttonRandomService clicked
     *  To associate this button action:
     *      open xml layout file and add an action to the button:
     *          android:onClick="onButtonRandomServiceClick"
     */
    fun onButtonRandomServiceClick(v: View) {
        Log.e(this.javaClass.simpleName, "onButtonRandomServiceClick(): going to call RandomBindLocalService...")
        if (mBound) {
            Log.i(this.javaClass.simpleName, "onButtonRandomServiceClick(): mBound=${mBound}")
            // Call a method from the LocalService.
            // NB: if this call does something that might hang, then the request should
            //  occur in a separate thread to avoid slowing down the activity performance.
            val num: Int = mService.randomNumber
            Toast.makeText(this, "onButtonRandomServiceClick(): number: $num", Toast.LENGTH_SHORT).show()
            val textViewRandomNumber = findViewById<TextView>(R.id.textViewRandomNumber)
            textViewRandomNumber.text = "$num"
        }
    }
}