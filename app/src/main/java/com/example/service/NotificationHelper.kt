package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object NotificationHelper {
    private const val CHANNEL_ORDERS = "mzn_vendor_orders_channel"
    private const val CHANNEL_STOCK = "mzn_vendor_stock_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderChannel = NotificationChannel(
                CHANNEL_ORDERS,
                "New Order Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts when customers place new orders on MznLive"
                enableVibration(true)
            }

            val stockChannel = NotificationChannel(
                CHANNEL_STOCK,
                "Inventory & Low Stock Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when catalog products fall below safe threshold"
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(orderChannel)
            manager.createNotificationChannel(stockChannel)
        }
    }

    fun showOrderNotification(
        context: Context,
        orderId: Long,
        orderNumber: String,
        customerName: String,
        amount: Double
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ORDERS)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("🔔 New Order #$orderNumber Received!")
            .setContentText("$customerName just ordered items worth $${String.format("%.2f", amount)}. Tap to fulfill.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Order #$orderNumber from $customerName for $${String.format("%.2f", amount)} was just placed via MznLive. Open MznLiveVendors to prepare the shipment.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(orderId.toInt(), builder.build())
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }

    fun showLowStockNotification(
        context: Context,
        productId: Long,
        productName: String,
        unitsLeft: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + 1000).toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_STOCK)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("⚠️ Low Stock Alert: $productName")
            .setContentText("Only $unitsLeft units remaining in warehouse! Restock now to prevent missed sales.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify((productId + 1000).toInt(), builder.build())
        } catch (_: SecurityException) {
            // Permission not yet granted
        }
    }
}
