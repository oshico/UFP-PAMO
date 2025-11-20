package edu.ufp.pam.examples.p04_masterdetail.dbcontacts

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Simply use an Application to create an instance of the DB and Repository.
 *
 * Then update AndroidManifest file by inserting the *android:name* into <application>:
 *      <application
 *         android:name=".p04_masterdetail.dbcontacts.CustomersApplication"
 *         android:allowBackup="true"
 *         ...
 */
class CustomersApplication : Application() {

    // No need to cancel this scope since it will be killed with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // The DB and Repository are only created when needed ('by lazy') rather than when app starts
    val database by lazy { CustomersDatabase.getCustomerDatabaseInstance(this, applicationScope) }
    val repository by lazy { CustomersRepository(database.customerDao()) }
}