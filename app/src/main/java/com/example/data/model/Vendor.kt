package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendors")
data class Vendor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val storeName: String,
    val email: String,
    val phone: String,
    val whatsappNumber: String = "",
    val category: String,
    val storeAddress: String,
    val upiId: String,
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val qrCodeData: String = "",
    val isVerified: Boolean = true,
    val isOpen: Boolean = true,
    val isProfileCompleted: Boolean = true,
    val rating: Float = 4.8f,
    val totalSalesCount: Int = 1420,
    val joinedDate: Long = System.currentTimeMillis()
)
