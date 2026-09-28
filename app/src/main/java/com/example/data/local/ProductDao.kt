package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getProductsByVendor(vendorId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    fun getProductById(productId: Long): Flow<Product?>

    @Query("SELECT * FROM products WHERE vendorId = :vendorId AND stockQuantity <= lowStockThreshold AND stockQuantity > 0")
    fun getLowStockProducts(vendorId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE vendorId = :vendorId AND stockQuantity <= 0")
    fun getOutOfStockProducts(vendorId: Long): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products WHERE vendorId = :vendorId")
    fun getProductCount(vendorId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE vendorId = :vendorId AND stockQuantity <= lowStockThreshold")
    fun getLowStockCount(vendorId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = :newQuantity WHERE id = :productId")
    suspend fun updateStock(productId: Long, newQuantity: Int)

    @Query("UPDATE products SET sellingPrice = :newPrice WHERE id = :productId")
    suspend fun updatePrice(productId: Long, newPrice: Double)

    @Query("UPDATE products SET isLiveDeal = :isLiveDeal WHERE id = :productId")
    suspend fun updateLiveDeal(productId: Long, isLiveDeal: Boolean)
}
