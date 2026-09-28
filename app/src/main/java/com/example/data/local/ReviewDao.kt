package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomerReview
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getReviewsByVendor(vendorId: Long): Flow<List<CustomerReview>>

    @Query("SELECT * FROM reviews WHERE vendorId = :vendorId AND rating = :rating ORDER BY createdAt DESC")
    fun getReviewsByRating(vendorId: Long, rating: Int): Flow<List<CustomerReview>>

    @Query("SELECT COUNT(*) FROM reviews WHERE vendorId = :vendorId")
    fun getTotalReviewsCount(vendorId: Long): Flow<Int>

    @Query("SELECT COALESCE(AVG(rating), 5.0) FROM reviews WHERE vendorId = :vendorId")
    fun getAverageRating(vendorId: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: CustomerReview): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<CustomerReview>)

    @Update
    suspend fun updateReview(review: CustomerReview)

    @Query("UPDATE reviews SET vendorReply = :reply, repliedAt = :repliedAt WHERE id = :reviewId")
    suspend fun addVendorReply(reviewId: Long, reply: String, repliedAt: Long = System.currentTimeMillis())
}
