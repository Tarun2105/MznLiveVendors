package com.example.data.repository

import android.content.Context
import com.example.data.local.MznVendorDatabase
import com.example.data.model.Coupon
import com.example.data.model.CustomerReview
import com.example.data.model.NotificationAlert
import com.example.data.model.Order
import com.example.data.model.PaymentTransaction
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.service.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.random.Random

class VendorRepository(
    private val database: MznVendorDatabase,
    private val appContext: Context
) {
    private val vendorDao = database.vendorDao()
    private val productDao = database.productDao()
    private val orderDao = database.orderDao()
    private val reviewDao = database.reviewDao()
    private val couponDao = database.couponDao()
    private val notificationDao = database.notificationDao()
    private val paymentDao = database.paymentDao()

    val currentVendor: Flow<Vendor?> = vendorDao.getFirstVendor()

    fun getProducts(vendorId: Long): Flow<List<Product>> = productDao.getProductsByVendor(vendorId)
    fun getOrders(vendorId: Long): Flow<List<Order>> = orderDao.getOrdersByVendor(vendorId)
    fun getReviews(vendorId: Long): Flow<List<CustomerReview>> = reviewDao.getReviewsByVendor(vendorId)
    fun getCoupons(vendorId: Long): Flow<List<Coupon>> = couponDao.getCouponsByVendor(vendorId)
    fun getNotifications(vendorId: Long): Flow<List<NotificationAlert>> = notificationDao.getNotificationsByVendor(vendorId)
    fun getUnreadNotificationsCount(vendorId: Long): Flow<Int> = notificationDao.getUnreadCount(vendorId)
    fun getLowStockCount(vendorId: Long): Flow<Int> = productDao.getLowStockCount(vendorId)
    fun getPayments(vendorId: Long): Flow<List<PaymentTransaction>> = paymentDao.getPaymentsByVendor(vendorId)
    fun getTotalPayments(vendorId: Long): Flow<Double> = paymentDao.getTotalPaymentsReceived(vendorId)

    suspend fun getVendorSync(): Vendor? = withContext(Dispatchers.IO) {
        vendorDao.getFirstVendorSync()
    }

    suspend fun saveVendorProfile(
        businessName: String,
        ownerName: String,
        email: String,
        phone: String,
        whatsapp: String,
        category: String,
        address: String,
        upiId: String,
        accountNo: String,
        ifsc: String,
        qrCodeData: String
    ): Long = withContext(Dispatchers.IO) {
        val existing = vendorDao.getFirstVendorSync()
        if (existing != null) {
            val updated = existing.copy(
                storeName = businessName,
                name = ownerName,
                email = email.ifEmpty { existing.email },
                phone = phone.ifEmpty { existing.phone },
                whatsappNumber = whatsapp,
                category = category,
                storeAddress = address,
                upiId = upiId,
                bankAccountNumber = accountNo,
                bankIfsc = ifsc,
                qrCodeData = qrCodeData,
                isProfileCompleted = true
            )
            vendorDao.updateVendor(updated)
            existing.id
        } else {
            val newVendor = Vendor(
                storeName = businessName,
                name = ownerName,
                email = email,
                phone = phone,
                whatsappNumber = whatsapp,
                category = category,
                storeAddress = address,
                upiId = upiId,
                bankAccountNumber = accountNo,
                bankIfsc = ifsc,
                qrCodeData = qrCodeData,
                isProfileCompleted = true,
                isVerified = true,
                isOpen = true
            )
            vendorDao.insertVendor(newVendor)
        }
    }

    suspend fun loginOrRegister(
        name: String,
        storeName: String,
        email: String,
        phone: String,
        category: String,
        address: String,
        upiId: String
    ): Long = withContext(Dispatchers.IO) {
        val existing = vendorDao.findVendorByEmail(email)
        if (existing != null) {
            existing.id
        } else {
            val vendor = Vendor(
                name = name,
                storeName = storeName,
                email = email,
                phone = phone,
                category = category,
                storeAddress = address,
                upiId = upiId,
                isVerified = true,
                isOpen = true
            )
            vendorDao.insertVendor(vendor)
        }
    }

    suspend fun addProduct(product: Product) = withContext(Dispatchers.IO) {
        val id = productDao.insertProduct(product)
        if (product.isLowStock) {
            val alert = NotificationAlert(
                vendorId = product.vendorId,
                title = "⚠️ Low Stock Notice",
                message = "${product.name} initial stock is at threshold (${product.stockQuantity} units).",
                type = "INVENTORY",
                relatedId = id
            )
            notificationDao.insertNotification(alert)
        }
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
        if (product.isLowStock) {
            NotificationHelper.showLowStockNotification(
                appContext,
                product.id,
                product.name,
                product.stockQuantity
            )
            val alert = NotificationAlert(
                vendorId = product.vendorId,
                title = "⚠️ Low Stock Warning!",
                message = "${product.name} is down to ${product.stockQuantity} items. Restock immediately.",
                type = "INVENTORY",
                relatedId = product.id
            )
            notificationDao.insertNotification(alert)
        }
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun quickUpdateStock(productId: Long, newStock: Int, vendorId: Long, productName: String) = withContext(Dispatchers.IO) {
        val stock = if (newStock < 0) 0 else newStock
        productDao.updateStock(productId, stock)
        if (stock in 1..5) {
            NotificationHelper.showLowStockNotification(appContext, productId, productName, stock)
            notificationDao.insertNotification(
                NotificationAlert(
                    vendorId = vendorId,
                    title = "⚠️ Low Stock Warning!",
                    message = "$productName has only $stock units left.",
                    type = "INVENTORY",
                    relatedId = productId
                )
            )
        }
    }

    suspend fun updateProductPrice(productId: Long, newPrice: Double) = withContext(Dispatchers.IO) {
        productDao.updatePrice(productId, newPrice)
    }

    suspend fun updateLiveDeal(productId: Long, isLiveDeal: Boolean) = withContext(Dispatchers.IO) {
        productDao.updateLiveDeal(productId, isLiveDeal)
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    suspend fun addCoupon(coupon: Coupon) = withContext(Dispatchers.IO) {
        couponDao.insertCoupon(coupon)
    }

    suspend fun toggleCoupon(couponId: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        couponDao.toggleCouponStatus(couponId, isActive)
    }

    suspend fun deleteCoupon(coupon: Coupon) = withContext(Dispatchers.IO) {
        couponDao.deleteCoupon(coupon)
    }

    suspend fun replyToReview(reviewId: Long, reply: String) = withContext(Dispatchers.IO) {
        reviewDao.addVendorReply(reviewId, reply)
    }

    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead(vendorId: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(vendorId)
    }

    suspend fun clearNotifications(vendorId: Long) = withContext(Dispatchers.IO) {
        notificationDao.clearAllNotifications(vendorId)
    }

    suspend fun toggleStoreStatus(vendorId: Long, isOpen: Boolean) = withContext(Dispatchers.IO) {
        vendorDao.updateStoreStatus(vendorId, isOpen)
    }

    // Companion integration: simulate customer purchase from MznLive customer app
    suspend fun simulateCustomerOrder(vendorId: Long) = withContext(Dispatchers.IO) {
        val sampleCustomers = listOf(
            Triple("Aarav Mehta", "+91 98450 11223", "Flat 304, Palm Grove, MG Road"),
            Triple("Sneha Kapoor", "+91 99887 77665", "House 18, Sunset Boulevard, Whitefield"),
            Triple("Rohan Gupta", "+91 91234 56789", "Tower B-1102, Cyber Heights, Tech City"),
            Triple("Neha Singhal", "+91 98765 00112", "Villa 9, Lotus Springs, Lake View")
        )
        val customer = sampleCustomers.random()
        val orderNum = "MZ-${Random.nextInt(9100, 9999)}"

        // Pick 1 or 2 products
        val currentProducts = productDao.getProductsByVendor(vendorId).firstOrNull() ?: emptyList()
        val chosenProduct = if (currentProducts.isNotEmpty()) currentProducts.random() else null

        val summary = if (chosenProduct != null) {
            "1x ${chosenProduct.name}"
        } else {
            "1x MznLive Premium Deal"
        }
        val amount = chosenProduct?.sellingPrice ?: 49.99

        val order = Order(
            orderNumber = orderNum,
            vendorId = vendorId,
            customerName = customer.first,
            customerPhone = customer.second,
            deliveryAddress = customer.third,
            totalAmount = amount + 5.0,
            discountAmount = 5.0,
            finalAmount = amount,
            paymentMethod = listOf("UPI", "CARD", "COD").random(),
            paymentStatus = "PAID",
            orderStatus = "NEW",
            itemsSummary = summary,
            itemCount = 1,
            customerNotes = "Placed via MznLive Customer App",
            createdAt = System.currentTimeMillis()
        )
        val orderId = orderDao.insertOrder(order)

        // Decrement product stock if available
        if (chosenProduct != null && chosenProduct.stockQuantity > 0) {
            val newQty = chosenProduct.stockQuantity - 1
            productDao.updateStock(chosenProduct.id, newQty)
            if (newQty in 1..chosenProduct.lowStockThreshold) {
                NotificationHelper.showLowStockNotification(appContext, chosenProduct.id, chosenProduct.name, newQty)
                notificationDao.insertNotification(
                    NotificationAlert(
                        vendorId = vendorId,
                        title = "⚠️ Low Stock Alert: ${chosenProduct.name}",
                        message = "Only $newQty units remaining after order #$orderNum.",
                        type = "INVENTORY",
                        relatedId = chosenProduct.id
                    )
                )
            }
        }

        // Trigger Notification
        NotificationHelper.showOrderNotification(
            appContext,
            orderId,
            orderNum,
            customer.first,
            amount
        )

        val receiptNum = "MZ-REC-${Random.nextInt(1000, 9999)}"
        paymentDao.insertPayment(
            PaymentTransaction(
                receiptNumber = receiptNum,
                vendorId = vendorId,
                orderNumber = orderNum,
                customerName = customer.first,
                customerPhone = customer.second,
                amount = amount,
                upiId = "mznprime@upi",
                upiRefNo = "UPI/${System.currentTimeMillis()}/MZN",
                paymentMethod = order.paymentMethod,
                status = "SUCCESS",
                itemsSummary = summary,
                timestamp = System.currentTimeMillis()
            )
        )

        notificationDao.insertNotification(
            NotificationAlert(
                vendorId = vendorId,
                title = "🔔 New Order #$orderNum Received!",
                message = "${customer.first} paid $${String.format("%.2f", amount)} via ${order.paymentMethod}. Receipt #$receiptNum issued.",
                type = "ORDER",
                relatedId = orderId
            )
        )
    }
}
