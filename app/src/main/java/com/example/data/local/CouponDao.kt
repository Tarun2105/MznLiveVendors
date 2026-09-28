package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Coupon
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getCouponsByVendor(vendorId: Long): Flow<List<Coupon>>

    @Query("SELECT * FROM coupons WHERE vendorId = :vendorId AND isActive = 1 ORDER BY createdAt DESC")
    fun getActiveCoupons(vendorId: Long): Flow<List<Coupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: Coupon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<Coupon>)

    @Update
    suspend fun updateCoupon(coupon: Coupon)

    @Delete
    suspend fun deleteCoupon(coupon: Coupon)

    @Query("UPDATE coupons SET isActive = :isActive WHERE id = :couponId")
    suspend fun toggleCouponStatus(couponId: Long, isActive: Boolean)

    @Query("UPDATE coupons SET timesUsed = timesUsed + 1 WHERE id = :couponId")
    suspend fun incrementCouponUsage(couponId: Long)
}
