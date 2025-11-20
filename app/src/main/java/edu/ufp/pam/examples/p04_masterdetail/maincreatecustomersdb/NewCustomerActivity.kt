package edu.ufp.pam.examples.p04_masterdetail.maincreatecustomersdb


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.ufp.pam.examples.R

class NewCustomerActivity : AppCompatActivity() {

    /** TODO: add EditText to enter new Customer name */
    private lateinit var editTextNewCustomerName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_customer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //TODO: Get EditText with new Customer name
        editTextNewCustomerName = findViewById(R.id.editTextNewCustomerName)

        //TODO: Set button action
        val buttonNewCustomer = findViewById<Button>(R.id.buttonNewCustomer)
        buttonNewCustomer.setOnClickListener {
            Log.i(this.javaClass.simpleName, "onClick(): buttonNewCustomer...")
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTextNewCustomerName.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val customerName = editTextNewCustomerName.text.toString()
                replyIntent.putExtra(EXTRA_CUSTOMER_NAME_REPLY_KEY, customerName)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    /** TODO: Create companion object to create constant for Intent extra key */
    companion object {
        const val EXTRA_CUSTOMER_NAME_REPLY_KEY = "edu.ufp.pam.examples.masterdetail.dbcontacts.REPLY"
    }
}