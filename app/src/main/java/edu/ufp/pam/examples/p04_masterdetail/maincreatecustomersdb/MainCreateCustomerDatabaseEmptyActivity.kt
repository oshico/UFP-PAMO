package edu.ufp.pam.examples.p04_masterdetail.maincreatecustomersdb


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.ufp.pam.examples.R
import edu.ufp.pam.examples.p04_masterdetail.dbcontacts.Customer
import edu.ufp.pam.examples.p04_masterdetail.dbcontacts.CustomersApplication
import edu.ufp.pam.examples.p04_masterdetail.viewmodel.CustomersViewModel
import edu.ufp.pam.examples.p04_masterdetail.viewmodel.CustomersViewModelFactory
import kotlin.getValue

/**
 * Empty View Activity to create the Room Database:
 *  - When Activity first starts, the ViewModelProvider creates the ViewModel.
 *  - When Activity is destroyed (e.g. configuration change) the ViewModel persists.
 *  - When activity is re-created, the ViewModelProvider returns the existing ViewModel.
 */
class MainCreateCustomersDatabaseEmptyActivity : AppCompatActivity() {

    /**
     * TODO: Create ViewModel to decouple Activity from DB...
     *  The CustomersViewModel is scoped to this Activity.
     *  By default ViewModelProvider creates ViewModels scoped to lifecycle of Activity/Fragment.
     */
    private val customersViewModel: CustomersViewModel by viewModels {
        CustomersViewModelFactory((application as CustomersApplication).repository)
    }

    /** TODO: create Request code for new Customer Activity
     *   (Key code used for hashmap communication between activities) */
    private val newCustomerActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_create_customers_database_empty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO:
        // 1. Create RecyclerView layout (recyclerview_customer_item)
        // 2. Get reference to recyclerViewCustomerItems
        val customerRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewCustomerItems)

        //TODO:
        // 1. Create ListAdapter class (CustomerListAdapter)
        // 2. Create instance of CustomerListAdapter and set adapter and layoutManager
        val customerListAdapter = CustomerListAdapter()
        customerRecyclerView.adapter = customerListAdapter
        customerRecyclerView.layoutManager = LinearLayoutManager(this)

        //TODO: Add Observer for LiveData associated with customersViewModel.allCustomers.
        //  The onChanged() is triggered whenever observed data changes with activity in foreground.
        customersViewModel.allCustomers.observe(
            this,
            Observer { customers : List<Customer> ->
                // Update cached list of customers
                customers?.let {
                    Log.i(this.javaClass.simpleName, "update(): allCustomers Observer...")
                    //Submit the new list of customers to be diffed and displayed
                    customerListAdapter.submitList(it)
                }
            })

        //TODO:
        // 1. Create NewCustomerActivity and associated layout (activity_new_customer.xml)
        // 2. Use FAB button to launch NewCustomerActivity (to insert data for new Customer)
        val fab = findViewById<FloatingActionButton>(R.id.fab_new_customer)
        fab.setOnClickListener {
            Log.i(this.javaClass.simpleName, "onClick(): going to create new customer...")
            // TODO: Create an Intent to launch the NewCustomerActivity
            val intent = Intent(this@MainCreateCustomersDatabaseEmptyActivity, NewCustomerActivity::class.java)
            startActivityForResult(intent, newCustomerActivityRequestCode)
        }
    }


    /**
     * TODO: When get back to MainCreateCustomersDatabaseEmptyActivity from NewCustomerActivity:
     *  - if RESULT_OK then insert new customer into DB by calling insert() on ViewModel;
     *  - else show an error Toast message.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        Log.i(this.javaClass.simpleName, "onActivityResult(): back from NewCustomerActivity...")
        if (requestCode == newCustomerActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val customerName : String? =
                    data.getStringExtra(NewCustomerActivity.EXTRA_CUSTOMER_NAME_REPLY_KEY)
                Log.i(this.javaClass.simpleName,"onActivityResult(): new customer = $customerName")

                //Get Customer index to automatically generate text for other fields
                val customerIndex : String? = customerName!!.subSequence(
                    customerName.length - 2,
                    customerName.length
                ).toString()

                //Customer(i, "Tio Patinhas $i", "Patinhas $i Lda", "Rua Sesamo $i", "Porto", "+35122000000$i")
                val customer = Customer(
                    0,
                    customerName,
                    "Patinhas ${customerIndex} Lda",
                    "Rua Sesamo $customerIndex",
                    "Porto",
                    "+35122000000$customerIndex",
                    null
                )
                //Insert new Customer into DB through the ViewModel
                customersViewModel.insert(customer)
                Unit
            }
        } else {
            Toast.makeText(applicationContext, "Empty customer... not saved!!", Toast.LENGTH_LONG).show()
        }
    }
}
