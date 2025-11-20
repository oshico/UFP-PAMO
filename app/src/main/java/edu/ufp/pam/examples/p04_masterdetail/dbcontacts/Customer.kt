package edu.ufp.pam.examples.p04_masterdetail.dbcontacts

import android.graphics.Bitmap
import androidx.room.*

@Entity(
    tableName = "customers",
    indices = [Index(value = ["customername", "customercompany"])]
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "customerid") var customerId: Int = 0,
    @ColumnInfo(name = "customername") var customerName: String? = "",
    @ColumnInfo(name = "customercompany") var customerCompany: String? = "",
    @ColumnInfo(name = "customeraddress") var customerAddress: String? = "",
    @ColumnInfo(name = "customercity") var customerCity: String? = "",
    @ColumnInfo(name = "customerphone") var customerPhone: String? = "",
    @Ignore var picture: Bitmap? = null
) {
    constructor() : this(0, "", "", "", "", "", null)
    override fun toString(): String = "${customerName!!}, ${customerCompany!!}, ${customerAddress!!}, ${customerCity!!}, ${customerPhone!!},"
}