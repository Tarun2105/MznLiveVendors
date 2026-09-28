package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Vendor
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    fun getVendorById(id: Long): Flow<Vendor?>

    @Query("SELECT * FROM vendors WHERE email = :email LIMIT 1")
    suspend fun findVendorByEmail(email: String): Vendor?

    @Query("SELECT * FROM vendors LIMIT 1")
    fun getFirstVendor(): Flow<Vendor?>

    @Query("SELECT * FROM vendors LIMIT 1")
    suspend fun getFirstVendorSync(): Vendor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: Vendor): Long

    @Update
    suspend fun updateVendor(vendor: Vendor)

    @Query("UPDATE vendors SET isOpen = :isOpen WHERE id = :vendorId")
    suspend fun updateStoreStatus(vendorId: Long, isOpen: Boolean)
}
