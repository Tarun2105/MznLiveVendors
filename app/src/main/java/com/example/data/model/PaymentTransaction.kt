package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String,
    val vendorId: Long,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val amount: Double,
    val upiId: String,
    val upiRefNo: String,
    val paymentMethod: String = "UPI QR", // UPI QR, DIRECT_UPI, CARD, NET_BANKING
    val status: String = "SUCCESS",
    val itemsSummary: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val verifiedByMerchant: Boolean = true
)
