package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.NotificationAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE vendorId = :vendorId ORDER BY timestamp DESC")
    fun getNotificationsByVendor(vendorId: Long): Flow<List<NotificationAlert>>

    @Query("SELECT COUNT(*) FROM notifications WHERE vendorId = :vendorId AND isRead = 0")
    fun getUnreadCount(vendorId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationAlert>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE vendorId = :vendorId")
    suspend fun markAllAsRead(vendorId: Long)

    @Query("DELETE FROM notifications WHERE vendorId = :vendorId")
    suspend fun clearAllNotifications(vendorId: Long)
}
