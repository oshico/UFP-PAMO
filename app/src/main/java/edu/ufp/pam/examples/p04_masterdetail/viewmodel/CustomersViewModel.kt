package edu.ufp.pam.examples.p04_masterdetail.viewmodel


import android.util.Log
import androidx.lifecycle.*
import edu.ufp.pam.examples.p04_masterdetail.dbcontacts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This View Model keeps a reference to the repository and an up-to-date list of all customers.
 * The ViewModel acts as communication hub/center between the Repository and UI.
 *
 * The ViewModel provides data in a lifecycle-conscious way to UI (survives configuration changes):
 * used to share data between instances of activity or between fragments.
 *
 * Activities and fragments are responsible for drawing data into screen,
 * while ViewModel takes care of holding and processing the data needed for the UI.
 *
 * The ViewModel uses LiveData for changeable data to be displayed in UI:
 *  - The UI can use observer to receive data updates;
 *  - The Repository and UI are completely separated by the ViewModel;
 *  - The ViewModel does not make DB calls (all handled in Repository) making code more testable.
 *
 *
 * The viewModelScope property:
 *  In Kotlin, all coroutines run inside a CoroutineScope. The scope controls the lifetime of
 *  coroutines through its job (canceling job scope => cancels all its coroutines).
 *
 *  The AndroidX lifecycle-viewmodel-ktx library adds a *viewModelScope* extension function of
 *  ViewModel class that enables working with scopes.
 *
 *
 * WARNING:
 * DO NOT keep a reference to a context that has a shorter lifecycle than the ViewModel
 * e.g. Activity, Fragment, View... all these objects can be destroyed and recreated by OS
 * when there is a configuration change. Hence, it can cause memory leaks.
 * INSTEAD, use *AndroidViewModel* when you need the app context, which has a lifecycle that
 * lives as long as app does.
 *
 * The ViewModels DO NOT survive app process being killed in the background by OS.
 */
class CustomersViewModel(private val repository: CustomersRepository) : ViewModel() {

    // ToDo: Use LiveData and cache customers returned by repository:
    //  - The UI attaches observers to receive data updates (instead of polling for changes).
    //  - The ViewModel separates the UI from the Repository.
    var allCustomers: LiveData<List<Customer>> = repository.allCustomers

    /** ToDo: Init cache with customers */
    //val customersCacheContent = CustomersCacheContent()

    /** ToDo: Launch new (non-blocking) coroutine to insert a customer */
    fun insert(customer: Customer) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(this.javaClass.simpleName,"launch(): async insert new customer ${customer}")
            repository.insertCustomer(customer)
        }
}

/**
 * TODO: Create a ViewModelFactory to instantiate CustomersViewModel
 *  The Jetpack framework uses ViewModel and ViewModelProvider.Factory to control the lifecycle
 *  of theses objects => they will survive to changes in config (e.g. screen rotations) and even
 *  if Activity is recreated it will receive the same ViewModel instance.
 */
class CustomersViewModelFactory(private val repository: CustomersRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}