package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val name: String,
    val category: String,
    val sku: String,
    val description: String,
    val originalPrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val isLiveDeal: Boolean = false,
    val isService: Boolean = false, // Product vs Service
    val offerDetails: String = "", // e.g. "Buy 1 Get 1", "Festival 20% off", "Free Home Setup"
    val imageUrl: String = "", // custom upload or preset key
    val status: String = "ACTIVE", // ACTIVE, DRAFT, OUT_OF_STOCK
    val rating: Float = 4.7f,
    val reviewsCount: Int = 28,
    val colorCode: Long = 0xFF0096C7, // Color accent for image representation
    val createdAt: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean get() = !isService && stockQuantity in 1..lowStockThreshold
    val isOutOfStock: Boolean get() = !isService && stockQuantity <= 0
    val discountPercent: Int
        get() = if (originalPrice > sellingPrice && originalPrice > 0) {
            (((originalPrice - sellingPrice) / originalPrice) * 100).toInt()
        } else 0
}
