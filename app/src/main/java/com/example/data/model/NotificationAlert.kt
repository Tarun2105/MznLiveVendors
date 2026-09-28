package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val title: String,
    val message: String,
    val type: String, // ORDER, INVENTORY, REVIEW, PROMO
    val isRead: Boolean = false,
    val relatedId: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)
