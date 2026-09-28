package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE vendorId = :vendorId ORDER BY timestamp DESC")
    fun getPaymentsByVendor(vendorId: Long): Flow<List<PaymentTransaction>>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    fun getPaymentById(id: Long): Flow<PaymentTransaction?>

    @Query("SELECT * FROM payments WHERE receiptNumber = :receiptNumber LIMIT 1")
    suspend fun getPaymentByReceipt(receiptNumber: String): PaymentTransaction?

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE vendorId = :vendorId AND status = 'SUCCESS'")
    fun getTotalPaymentsReceived(vendorId: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM payments WHERE vendorId = :vendorId")
    fun getPaymentCount(vendorId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentTransaction>)
}
