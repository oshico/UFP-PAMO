package edu.ufp.pam.examples.p04_masterdetail.dbcontacts

import androidx.room.DatabaseView

@DatabaseView("SELECT customertasks.customertaskid, customertasks.customertasktitle, customertasks.customertaskdesc, "+
        "customertasks.customerid, customers.customername AS customername FROM customertasks " +
        "INNER JOIN customers ON customertasks.customerid = customers.customerid")
data class CustomerTaskDetailView(
    val customertaskid: Int,
    val customertasktitle: String?,
    val customertaskdesc: String?,
    val customerid: Int,
    val customername: String?
)