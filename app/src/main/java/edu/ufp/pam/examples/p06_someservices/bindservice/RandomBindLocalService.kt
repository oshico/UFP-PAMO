package edu.ufp.pam.examples.p06_someservices.bindservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.Random

class RandomBindLocalService : Service() {

    // Binder given to clients
    private val binder = RandomLocalBinder()

    // Random number generator
    private val mGenerator = Random()

    /** Property for clients to use */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    /**
     * Class used for the client Binder, since this service will always
     * runs in the same process of its clients (no need to deal with IPC).
     */
    inner class RandomLocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): RandomBindLocalService = this@RandomBindLocalService
    }

    override fun onBind(intent: Intent): IBinder {
        //Return the communication channel to the service...
        return binder
    }
}