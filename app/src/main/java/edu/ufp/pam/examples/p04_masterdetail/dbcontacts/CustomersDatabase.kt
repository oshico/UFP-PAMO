package edu.ufp.pam.examples.p04_masterdetail.dbcontacts


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log

/**
 * This is the Room database that contains the tables for all entities
 * (e.g. Customer, CustomerTask, etc.).
 *
 * Get an instance of created database using following code:
 *   val db =
 *   Room.databaseBuilder(applicationContext, CustomersDatabase::class.java, "Customers.db").build()
 *
 * Whenever it is needed to modify the database schema:
 *      1) update the version number and
 *      2) define a migration strategy
 *
 * When experiencing errors in Android Studio execute:
 *      Build > Clean Project
 *      Build > Rebuild Project
 */
@Database(
    //views = [CustomerTaskDetailView::class],
    entities = [Customer::class],
    //entities = [Customer::class, CustomerTask::class],
    version = 18,
    //To remove warning "... do not export schema...":
    //exportSchema = false,
    //Instead define schema location inside build.gradle[Module] file:
    //       /* Do not use Kapt anymore */
    //       kapt {
    //            arguments {
    //                arg("room.schemaLocation", "$projectDir/schemas".toString())
    //                arg("room.incremental", "true")
    //                arg("room.expandProjection", "true")
    //            }
    //        }
    //        /* Use KSP with Room (deal with Room annotations) */
    //        ksp {
    //            arg("room.schemaLocation", "$projectDir/schemas")
    //        }
)
abstract class CustomersDatabase : RoomDatabase() {

    /** The DAO to access Customer entities. */
    abstract fun customerDao(): CustomerDao
    //abstract fun customerTaskDao(): CustomerTaskDao
    //abstract fun customerTaskDetailViewDao(): CustomerTaskDetailViewDao

    //Behaves like a static attribute
    /** Companion object to create/get the CustomersDatabase singleton.
     *  Behaves like a static attribute */
    companion object {
        @Volatile
        private var INSTANCE: CustomersDatabase? = null

        /** Public method to get the DB without initiate pre-population. */
        fun getCustomerDatabaseInstance(context: Context): CustomersDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        /** Private method to create the DB without any pre-population. */
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CustomersDatabase::class.java,
                "Customers.db"
            ).fallbackToDestructiveMigration()
                //May use migration objets on each new schema
                //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()

        /** Public method to get the DB with pre-population using a CoroutineScope. */
        fun getCustomerDatabaseInstance(context: Context, scope: CoroutineScope): CustomersDatabase =
            INSTANCE ?: synchronized(this) {
                //Singleton impl: if instance not null then return it, else create new instance
                INSTANCE ?: buildDatabase(context, scope).also { INSTANCE = it }
            }

        /** Private method to create the DB with pre-population using a CoroutineScope.
         *  Populates DB through the use of RoomDatabase.Callback in Room.databaseBuilder(). */
        private fun buildDatabase(context: Context, scope: CoroutineScope) =
            Room.databaseBuilder(
                context.applicationContext,
                CustomersDatabase::class.java,
                "Customers.db"
            ).fallbackToDestructiveMigration()
                //Use migration objects for each new schema evolution
                //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                //Use RoomDatabase.Callback() to clear and repopulate DB instead of migrating
                .addCallback(CustomersDatabaseCallback(scope))
                .build()
    }

    /**
     * The RoomDatabase.Callback() is called on DB databaseBuilder():
     *  1. override onOpen(): clear and repopulate DB whenever app is started;
     *  2. override onCreate(): populate DB only the first time the app is launched.
     */
    private class CustomersDatabaseCallback(private val scope: CoroutineScope) :
        Callback() {

        /** Override onOpen() to clear and repopulate DB every time app is started. */
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    // To keep DB data through app restarts comment coroutine exec:
                    cleanAndPopulateCustomersDatabase(database.customerDao())
                }
            }
        }

        /** Override onCreate() to populate DB only the first time app is launched. */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    //To clear and repopulate DB every time app is started comment coroutine exec:
                    //cleanAndPopulateCustomersDatabase(database.customerDao())
                }
            }
        }

        /**
         * Remove all customers from DB and populate with some customers.
         */
        fun cleanAndPopulateCustomersDatabase(customerDao: CustomerDao) {
            // Clear all customers from DB
            customerDao.deleteAllcustomers()
            //Populate with some Patinhas customers
            for (i in 1..8) {
                //CREATE Customer object
                val customer: Customer =
                    Customer(
                        i, "Tio Patinhas $i", "Patinhas $i Lda",
                        "Rua Sesamo $i", "Porto", "+35122000000$i"
                    )
                Log.i(this.javaClass.simpleName, "addSampleItemsToDatabase(): create customer = $customer")
                //INSERT Customer into DB
                val id: Long = customerDao.insertCustomer(customer)
                Log.i(this.javaClass.simpleName, "addSampleItemsToDatabase(): added record id = $id")
            }
        }
    }

    /** Migration object from DB version 1 to 2. */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `tasktypes` (`id` INTEGER, `tasktitle` TEXT, " +
                        "PRIMARY KEY(`id`))"
            )
        }
    }

    /** Migration object from DB version 2 to 3. */
    val MIGRATION_2_3: Migration
        get() = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE tasktypes ADD COLUMN taskpriority INTEGER"
                )
            }
        }
}
