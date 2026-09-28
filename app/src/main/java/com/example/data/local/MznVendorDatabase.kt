package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Coupon
import com.example.data.model.CustomerReview
import com.example.data.model.NotificationAlert
import com.example.data.model.Order
import com.example.data.model.PaymentTransaction
import com.example.data.model.Product
import com.example.data.model.Vendor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Vendor::class,
        Product::class,
        Order::class,
        CustomerReview::class,
        Coupon::class,
        NotificationAlert::class,
        PaymentTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MznVendorDatabase : RoomDatabase() {
    abstract fun vendorDao(): VendorDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun couponDao(): CouponDao
    abstract fun notificationDao(): NotificationDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: MznVendorDatabase? = null

        fun getInstance(context: Context): MznVendorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MznVendorDatabase::class.java,
                    "mzn_vendor_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                prepopulateDatabase(getInstance(context))
            }
        }

        private suspend fun prepopulateDatabase(database: MznVendorDatabase) {
            val vendorDao = database.vendorDao()
            val productDao = database.productDao()
            val orderDao = database.orderDao()
            val reviewDao = database.reviewDao()
            val couponDao = database.couponDao()
            val notificationDao = database.notificationDao()

            val vendorId = vendorDao.insertVendor(
                Vendor(
                    id = 1,
                    name = "Zaid Khan",
                    storeName = "Mzn Prime Superstore",
                    email = "vendor@mznlive.com",
                    phone = "+91 98765 43210",
                    category = "Electronics & Lifestyle",
                    storeAddress = "Shop 14, Tech Plaza, Downtown Market",
                    upiId = "mznprime@upi",
                    isVerified = true,
                    isOpen = true,
                    rating = 4.8f,
                    totalSalesCount = 1420
                )
            )

            // Products
            val products = listOf(
                Product(
                    id = 1,
                    vendorId = vendorId,
                    name = "Mzn Aura ANC Wireless Earbuds",
                    category = "Electronics",
                    sku = "MZN-EAR-01",
                    description = "Active Noise Cancellation 40dB, 36hr battery, ultra-low latency gaming mode, IPX5 waterproof.",
                    originalPrice = 69.99,
                    sellingPrice = 49.99,
                    stockQuantity = 3, // LOW STOCK
                    lowStockThreshold = 5,
                    isLiveDeal = true,
                    status = "ACTIVE",
                    rating = 4.9f,
                    reviewsCount = 42,
                    colorCode = 0xFF0096C7
                ),
                Product(
                    id = 2,
                    vendorId = vendorId,
                    name = "Mzn Pro Ultra 4K Smart Watch",
                    category = "Electronics",
                    sku = "MZN-WTC-02",
                    description = "1.96 AMOLED display, Bluetooth calling, heart rate & SpO2 sensor, 100+ sports modes.",
                    originalPrice = 119.00,
                    sellingPrice = 89.00,
                    stockQuantity = 14,
                    lowStockThreshold = 5,
                    isLiveDeal = false,
                    status = "ACTIVE",
                    rating = 4.7f,
                    reviewsCount = 35,
                    colorCode = 0xFF4361EE
                ),
                Product(
                    id = 3,
                    vendorId = vendorId,
                    name = "Mzn SoundPulse 5.3 Speaker",
                    category = "Electronics",
                    sku = "MZN-SPK-03",
                    description = "30W deep bass 360 sound, RGB beat lighting, 18-hour continuous playtime, dust & splash resistant.",
                    originalPrice = 49.99,
                    sellingPrice = 34.99,
                    stockQuantity = 8,
                    lowStockThreshold = 5,
                    isLiveDeal = true,
                    status = "ACTIVE",
                    rating = 4.8f,
                    reviewsCount = 19,
                    colorCode = 0xFF3F37C9
                ),
                Product(
                    id = 4,
                    vendorId = vendorId,
                    name = "Premium Single Origin Cold Brew (500g)",
                    category = "Gourmet Food",
                    sku = "MZN-COF-04",
                    description = "100% Arabica artisan roasted coffee beans with rich dark chocolate and hazelnut notes.",
                    originalPrice = 24.00,
                    sellingPrice = 18.50,
                    stockQuantity = 2, // LOW STOCK
                    lowStockThreshold = 5,
                    isLiveDeal = false,
                    status = "ACTIVE",
                    rating = 5.0f,
                    reviewsCount = 58,
                    colorCode = 0xFFD4A373
                ),
                Product(
                    id = 5,
                    vendorId = vendorId,
                    name = "Mzn Live Signature Heavyweight Hoodie",
                    category = "Fashion",
                    sku = "MZN-APP-05",
                    description = "380 GSM fleece cotton, embroidered MznLive chest logo, relaxed drop-shoulder streetwear fit.",
                    originalPrice = 55.00,
                    sellingPrice = 42.00,
                    stockQuantity = 25,
                    lowStockThreshold = 6,
                    isLiveDeal = false,
                    status = "ACTIVE",
                    rating = 4.6f,
                    reviewsCount = 17,
                    colorCode = 0xFF1D3557
                ),
                Product(
                    id = 6,
                    vendorId = vendorId,
                    name = "Ergonomic Anodized Aluminium Laptop Stand",
                    category = "Accessories",
                    sku = "MZN-ACC-06",
                    description = "Foldable 6-angle height adjustment, silicone anti-slip grips, hollow heat dissipation design.",
                    originalPrice = 39.99,
                    sellingPrice = 29.99,
                    stockQuantity = 0, // OUT OF STOCK
                    lowStockThreshold = 5,
                    isLiveDeal = false,
                    status = "OUT_OF_STOCK",
                    rating = 4.8f,
                    reviewsCount = 23,
                    colorCode = 0xFF6C757D
                ),
                Product(
                    id = 7,
                    vendorId = vendorId,
                    name = "Smart RGB Ambient Flow Lightbars (Pair)",
                    category = "Electronics",
                    sku = "MZN-RGB-07",
                    description = "Syncs with music and screen, 16 million colors, app controlled, desktop ambient setup.",
                    originalPrice = 45.00,
                    sellingPrice = 32.50,
                    stockQuantity = 11,
                    lowStockThreshold = 4,
                    isLiveDeal = true,
                    status = "ACTIVE",
                    rating = 4.9f,
                    reviewsCount = 31,
                    colorCode = 0xFF7209B7
                )
            )
            productDao.insertProducts(products)

            // Orders
            val orders = listOf(
                Order(
                    id = 1,
                    orderNumber = "MZ-9042",
                    vendorId = vendorId,
                    customerName = "Priya Sharma",
                    customerPhone = "+91 99112 33445",
                    deliveryAddress = "Flat 402, Green Valley Apts, Sector 62",
                    totalAmount = 99.98,
                    discountAmount = 10.0,
                    finalAmount = 89.98,
                    paymentMethod = "UPI",
                    paymentStatus = "PAID",
                    orderStatus = "NEW",
                    itemsSummary = "2x Mzn Aura ANC Wireless Earbuds",
                    itemCount = 2,
                    customerNotes = "Please deliver before 6 PM.",
                    createdAt = System.currentTimeMillis() - 15 * 60 * 1000 // 15 mins ago
                ),
                Order(
                    id = 2,
                    orderNumber = "MZ-9039",
                    vendorId = vendorId,
                    customerName = "Rahul Verma",
                    customerPhone = "+91 98220 54321",
                    deliveryAddress = "12B, Rosewood Residency, Central Avenue",
                    totalAmount = 89.00,
                    discountAmount = 0.0,
                    finalAmount = 89.00,
                    paymentMethod = "CARD",
                    paymentStatus = "PAID",
                    orderStatus = "PREPARING",
                    itemsSummary = "1x Mzn Pro Ultra 4K Smart Watch",
                    itemCount = 1,
                    customerNotes = "Gift packaging requested.",
                    createdAt = System.currentTimeMillis() - 65 * 60 * 1000 // 1 hr ago
                ),
                Order(
                    id = 3,
                    orderNumber = "MZ-9028",
                    vendorId = vendorId,
                    customerName = "Ananya Patel",
                    customerPhone = "+91 97334 11223",
                    deliveryAddress = "Plot 88, Sunrise Enclave, Park Road",
                    totalAmount = 60.50,
                    discountAmount = 6.0,
                    finalAmount = 54.50,
                    paymentMethod = "UPI",
                    paymentStatus = "PAID",
                    orderStatus = "SHIPPED",
                    itemsSummary = "1x Mzn SoundPulse 5.3 Speaker, 1x Cold Brew Coffee",
                    itemCount = 2,
                    customerNotes = "",
                    createdAt = System.currentTimeMillis() - 4 * 3600 * 1000 // 4 hrs ago
                ),
                Order(
                    id = 4,
                    orderNumber = "MZ-8995",
                    vendorId = vendorId,
                    customerName = "Vikram Malhotra",
                    customerPhone = "+91 98110 99887",
                    deliveryAddress = "Villa 22, Whispering Pines, Hilltop",
                    totalAmount = 42.00,
                    discountAmount = 0.0,
                    finalAmount = 42.00,
                    paymentMethod = "COD",
                    paymentStatus = "PAID",
                    orderStatus = "DELIVERED",
                    itemsSummary = "1x Mzn Live Signature Heavyweight Hoodie",
                    itemCount = 1,
                    customerNotes = "",
                    createdAt = System.currentTimeMillis() - 26 * 3600 * 1000 // yesterday
                )
            )
            orderDao.insertOrders(orders)

            // Customer Reviews
            val reviews = listOf(
                CustomerReview(
                    id = 1,
                    vendorId = vendorId,
                    productName = "Mzn Aura ANC Wireless Earbuds",
                    customerName = "Arjun Mehta",
                    rating = 5,
                    comment = "Incredible active noise cancelling for this price! Battery lasts for days and connects instantly to my phone. 10/10 companion shopping experience.",
                    isVerifiedPurchase = true,
                    vendorReply = "Thank you so much Arjun! Glad you love the Aura ANC. Enjoy the beats!",
                    repliedAt = System.currentTimeMillis() - 2 * 3600 * 1000,
                    createdAt = System.currentTimeMillis() - 12 * 3600 * 1000
                ),
                CustomerReview(
                    id = 2,
                    vendorId = vendorId,
                    productName = "Mzn Pro Ultra 4K Smart Watch",
                    customerName = "Simran Kaur",
                    rating = 4,
                    comment = "Display is vibrant and crisp even under bright sunlight. Step tracking is very accurate. Would love more custom watch faces in the next firmware update.",
                    isVerifiedPurchase = true,
                    vendorReply = null,
                    createdAt = System.currentTimeMillis() - 18 * 3600 * 1000
                ),
                CustomerReview(
                    id = 3,
                    vendorId = vendorId,
                    productName = "Premium Single Origin Cold Brew (500g)",
                    customerName = "Karan Singhania",
                    rating = 5,
                    comment = "Best beans I've ordered online. Fresh aroma when opening the bag and super smooth crema when extracted. Ordering my second bag already!",
                    isVerifiedPurchase = true,
                    vendorReply = "Cheers Karan! We roast in fresh small batches weekly so you always get peak flavor.",
                    repliedAt = System.currentTimeMillis() - 30 * 3600 * 1000,
                    createdAt = System.currentTimeMillis() - 36 * 3600 * 1000
                )
            )
            reviewDao.insertReviews(reviews)

            // Coupons
            val coupons = listOf(
                Coupon(
                    id = 1,
                    vendorId = vendorId,
                    code = "MZNWELCOME20",
                    description = "20% off for first-time buyers on MznLive platform",
                    discountType = "PERCENTAGE",
                    discountValue = 20.0,
                    minOrderAmount = 30.0,
                    timesUsed = 148,
                    usageLimit = 500,
                    isActive = true
                ),
                Coupon(
                    id = 2,
                    vendorId = vendorId,
                    code = "FLASHSALE15",
                    description = "$15 flat discount on orders above $60",
                    discountType = "FLAT",
                    discountValue = 15.0,
                    minOrderAmount = 60.0,
                    timesUsed = 89,
                    usageLimit = 200,
                    isActive = true
                ),
                Coupon(
                    id = 3,
                    vendorId = vendorId,
                    code = "FREESHIP",
                    description = "Free expedited courier delivery on all electronics",
                    discountType = "FLAT",
                    discountValue = 5.0,
                    minOrderAmount = 25.0,
                    timesUsed = 312,
                    usageLimit = 1000,
                    isActive = true
                )
            )
            couponDao.insertCoupons(coupons)

            // Notifications
            val notifications = listOf(
                NotificationAlert(
                    id = 1,
                    vendorId = vendorId,
                    title = "🔔 New Order #MZ-9042 Received!",
                    message = "Priya Sharma placed an order for 2x Mzn Aura ANC Wireless Earbuds ($89.98). Prepare for dispatch.",
                    type = "ORDER",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000
                ),
                NotificationAlert(
                    id = 2,
                    vendorId = vendorId,
                    title = "⚠️ Low Stock Warning!",
                    message = "'Mzn Aura ANC Wireless Earbuds' has only 3 units left. Restock now to maintain search ranking.",
                    type = "INVENTORY",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 45 * 60 * 1000
                ),
                NotificationAlert(
                    id = 3,
                    vendorId = vendorId,
                    title = "⚠️ Low Stock Warning!",
                    message = "'Premium Single Origin Cold Brew' has only 2 units left in your warehouse.",
                    type = "INVENTORY",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 3 * 3600 * 1000
                ),
                NotificationAlert(
                    id = 4,
                    vendorId = vendorId,
                    title = "⭐ New Customer Review!",
                    message = "Simran Kaur left a 4-star review on 'Mzn Pro Ultra 4K Smart Watch'. Tap to reply directly.",
                    type = "REVIEW",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 18 * 3600 * 1000
                )
            )
            notificationDao.insertNotifications(notifications)

            // Initial Payments Received & Acknowledgement Receipts
            val paymentDao = database.paymentDao()
            val initialPayments = listOf(
                PaymentTransaction(
                    id = 1,
                    receiptNumber = "MZ-REC-9042",
                    vendorId = vendorId,
                    orderNumber = "MZ-9042",
                    customerName = "Priya Sharma",
                    customerPhone = "+91 99112 33445",
                    amount = 89.98,
                    upiId = "mznprime@upi",
                    upiRefNo = "UPI/392019402910/MZN",
                    paymentMethod = "UPI QR",
                    status = "SUCCESS",
                    itemsSummary = "2x Mzn Aura ANC Wireless Earbuds",
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000
                ),
                PaymentTransaction(
                    id = 2,
                    receiptNumber = "MZ-REC-9039",
                    vendorId = vendorId,
                    orderNumber = "MZ-9039",
                    customerName = "Rahul Verma",
                    customerPhone = "+91 98220 54321",
                    amount = 89.00,
                    upiId = "mznprime@upi",
                    upiRefNo = "CARD/TXN-8849204",
                    paymentMethod = "CARD",
                    status = "SUCCESS",
                    itemsSummary = "1x Mzn Pro Ultra 4K Smart Watch",
                    timestamp = System.currentTimeMillis() - 65 * 60 * 1000
                ),
                PaymentTransaction(
                    id = 3,
                    receiptNumber = "MZ-REC-9028",
                    vendorId = vendorId,
                    orderNumber = "MZ-9028",
                    customerName = "Ananya Patel",
                    customerPhone = "+91 97334 11223",
                    amount = 54.50,
                    upiId = "mznprime@upi",
                    upiRefNo = "UPI/948201849102/MZN",
                    paymentMethod = "DIRECT UPI",
                    status = "SUCCESS",
                    itemsSummary = "1x Mzn SoundPulse 5.3 Speaker, 1x Cold Brew Coffee",
                    timestamp = System.currentTimeMillis() - 4 * 3600 * 1000
                )
            )
            paymentDao.insertPayments(initialPayments)
        }
    }
}
