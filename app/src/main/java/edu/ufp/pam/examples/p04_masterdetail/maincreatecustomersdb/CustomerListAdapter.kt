package edu.ufp.pam.examples.p04_masterdetail.maincreatecustomersdb


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import edu.ufp.pam.examples.p04_masterdetail.dbcontacts.Customer

import edu.ufp.pam.examples.R

/**
 * TODO: Create the CustomerListAdapter to display Customer items in RecyclerView
 *  The CustomerListAdapter is responsible for creating the CustomerViewHolder inside
 *  the onCreateViewHolder() and associate it with onBindViewHolder()
 */
class CustomerListAdapter : ListAdapter<Customer, CustomerListAdapter.CustomerViewHolder>(
    CustomerComparator()
) {
    /**
     * Create the CustomerViewHolder and associate it with onBindViewHolder().
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder.create(parent)
    }

    /**
     * Associate the CustomerViewHolder with the Customer data.
     */
    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val current = getItem(position)
        Log.i(this.javaClass.simpleName, "onBindViewHolder(): current=$current")
        holder.bind(current.customerName)
    }

    //
    /** TODO:
     *      Create the CustomerViewHolder to contain Customer item view data within RecyclerView.
     *      It is a Inner class to associate Customer data to a TextView */
    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customerItemView: TextView = itemView.findViewById(R.id.textViewCustomerItem)
        private var currentText: String? = null

        //TODO: set OnClickListener to show a Toast with the Customer name when clicked
        init {
            customerItemView.setOnClickListener {
                val text = currentText ?: return@setOnClickListener
                Toast.makeText(itemView.context, "Clicked: $text", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(text: String?) {
            Log.i(this.javaClass.simpleName, "bind(): text=$text")
            currentText = text
            customerItemView.text = text
        }

        /** TODO: create companion object with create() method
         *      Static method to inflate layout R.layout.recyclerview_customer_item
         *      and create the CustomerViewHolder
         */
        companion object {
            fun create(parent: ViewGroup): CustomerViewHolder {
                Log.i(this.javaClass.simpleName, "create(): inflate layout R.layout.recyclerview_customer_item...")
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_customer_item, parent, false)
                return CustomerViewHolder(view)
            }
        }
    }

    /** TODO:
     *      create CustomerComparator inner class to compare two Customers (by object ref or by name)
     */
    class CustomerComparator : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            Log.i(this.javaClass.simpleName, "areItemsTheSame(): check ${oldItem.customerName} === ${newItem.customerName}")
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            Log.i(this.javaClass.simpleName, "areContentsTheSame(): check ${oldItem.customerName} === ${newItem.customerName}")
            return oldItem.customerName == newItem.customerName
        }
    }
}