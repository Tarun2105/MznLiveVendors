package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class CustomerReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val productName: String,
    val customerName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val isVerifiedPurchase: Boolean = true,
    val vendorReply: String? = null,
    val repliedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
