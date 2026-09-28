package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getOrdersByVendor(vendorId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE vendorId = :vendorId AND orderStatus = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(vendorId: Long, status: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: Long): Flow<Order?>

    @Query("SELECT COUNT(*) FROM orders WHERE vendorId = :vendorId")
    fun getTotalOrdersCount(vendorId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE vendorId = :vendorId AND orderStatus = 'NEW'")
    fun getNewOrdersCount(vendorId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(finalAmount), 0.0) FROM orders WHERE vendorId = :vendorId AND orderStatus != 'CANCELLED'")
    fun getTotalRevenue(vendorId: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET orderStatus = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: String)
}
