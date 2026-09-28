package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val vendorId: Long,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val finalAmount: Double,
    val paymentMethod: String = "UPI", // UPI, COD, CARD
    val paymentStatus: String = "PAID", // PAID, PENDING, REFUNDED
    val orderStatus: String = "NEW", // NEW, PREPARING, SHIPPED, DELIVERED, CANCELLED
    val itemsSummary: String, // e.g. "2x Wireless Earbuds, 1x Smart Watch"
    val itemCount: Int = 1,
    val customerNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
