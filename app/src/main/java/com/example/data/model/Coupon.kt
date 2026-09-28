package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val code: String,
    val description: String,
    val discountType: String = "PERCENTAGE", // PERCENTAGE, FLAT
    val discountValue: Double,
    val minOrderAmount: Double = 0.0,
    val timesUsed: Int = 0,
    val usageLimit: Int = 100,
    val isActive: Boolean = true,
    val validUntil: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
    val createdAt: Long = System.currentTimeMillis()
)
