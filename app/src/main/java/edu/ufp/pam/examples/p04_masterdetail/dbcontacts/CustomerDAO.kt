package edu.ufp.pam.examples.p04_masterdetail.dbcontacts


import androidx.room.*
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the customers table.
 *
 * Room allows Observable queries with LiveData (app's UI update automatically when data changes).
 *      * Use a return value of type *LiveData* in query methods description (Room generates all
 *      necessary code to update the LiveData when the database is updated).
 *      * When necessary to update data stored within LiveData, use MutableLiveData instead of
 *      LiveData (provides methods to set/postvalue(), usually used within ViewModel) .
 *
 * Room allows Reactive queries with RxJava
 *  Provides the following support for return values of RxJava2 types:
 *      Methods @Query: Publisher, Flowable, and Observable.
 *      Methods @Insert, @Update and @Delete: Completable, Single<T>, and Maybe<T>.
 *
 *      * Single: an Observable which only emits one item or throws an error (e.g. a network call,
 *          with retrofit, you return an Observable or Flowable; care for the response once
 *          you can replace this with Single<T>)
 *
 *      * Maybe: similar to Single but it allows for no emissions as well;
 *
 *      * Completable: only concerned with execution completion, i.e., whether the task has reach to
 *          completion or some error has occurred;
 *
 *      * Flowable: just like an Observable with backpressure mechanism (when observable
 *          generates huge amounts of events, flowable limits buffer size).
 *
 * Use async queries with Kotlin coroutines (instead of AsyncTask):
 *      Add *suspend* Kotlin keyword to DAO methods to make them asynchronous (Kotlin coroutines).
 *      This ensures that they cannot be executed on the main thread.
 *
 *      Room with Kotlin coroutines requires:
 *          Room 2.1.0, Kotlin 1.3.0, and Coroutines 1.0.0 or higher.
 */
@Dao
interface CustomerDao {

    /**
     * Get all customers.
     * @return all customers from the table.
     */
    @Query("SELECT * FROM customers")
    //suspend fun loadAllCustomers(): List<Customer>
    fun loadAllCustomers(): LiveData<List<Customer>>

    /**
     * Get all customers ordered ascendant.
     * @return all customers from the table in ascendant order.
     */
    @Query("SELECT * FROM customers ORDER BY customername ASC")
    //suspend fun loadAllCustomersOrdered(): List<Customer>
    fun loadAllCustomersOrdered(): LiveData<List<Customer>>

    /**
     * Get a customer by id.
     * @return the customer from the table with a specific id.
     */
    @Query("SELECT * FROM customers WHERE customerid = :id LIMIT 1")
    fun getCustomerById(id: String): Flow<Customer>

    /**
     * Get customers by city.
     * @return the customers from the table with city in cityname.
     */
    @Query("SELECT * FROM customers WHERE customercity LIKE :city")
    fun getCustomersByCity(city: String): Array<Customer>

    /**
     * Get customers by cities.
     * @return the customers from the table with specific cities.
     */
    @Query("SELECT * FROM customers WHERE customercity IN (:cities)")
    fun loadCustomersFromCities(cities: List<String>): List<Customer>

    /**
     * Get all customers minimal info.
     * @return the customers from the table customersminimal.
     */
    //@Query("SELECT customerid, customername, customercompany FROM customersminimal")
    //fun loadFullCustomerMinimalInfo(): List<CustomerMinimal>

    /**
     * Insert a customer in the database (returns id), replace it if already exists.
     * @param customer the customer to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomer(customer: Customer): Long

    /**
     * Insert a customer in the database (returns id), replace it if already exists.
     * @param customer the customer to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerCompletable(customer: Customer): Completable

    /**
     * Insert 1+ customers into database. If the customers already exists, replace them.
     * @param customers the set of customers to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomers(vararg customers: Customer): List<Long>

    /**
     * Update 1+ customers into database (returns number of updates rows).
     * @param customers the set of customers to be updated.
     */
    @Update
    fun updateCustomers(vararg customers: Customer): Int

    /**
     * Delete 1+ customers from database (returns number of deleted rows).
     * @param customers the set of customers to be deleted.
     */
    @Delete
    fun deleteCustomers(vararg customers: Customer): Int

    /**
     * Delete all customers.
     */
    @Query("DELETE FROM customers")
    fun deleteAllcustomers(): Int
}