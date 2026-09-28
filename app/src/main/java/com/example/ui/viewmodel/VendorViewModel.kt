package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MznVendorDatabase
import com.example.data.model.Coupon
import com.example.data.model.CustomerReview
import com.example.data.model.NotificationAlert
import com.example.data.model.Order
import com.example.data.model.PaymentTransaction
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.repository.VendorRepository
import com.example.service.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class VendorScreen(val title: String) {
    SPLASH("Splash"),
    AUTH_OTP("Authentication"),
    PROFILE_GENERATION("Profile Setup"),
    DASHBOARD("Dashboard"),
    INVENTORY("Shop & Services"),
    ORDERS("Orders"),
    PAYMENTS("Payments & Receipts"),
    ANALYTICS("Analytics"),
    FEEDBACK("Feedback"),
    MARKETING("Coupons & Deals"),
    NOTIFICATIONS("Alerts"),
    STORE_PROFILE("Store Profile")
}

class VendorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MznVendorDatabase.getInstance(application)
    private val repository = VendorRepository(database, application)

    init {
        NotificationHelper.createNotificationChannels(application)
    }

    private val _currentScreen = MutableStateFlow(VendorScreen.SPLASH)
    val currentScreen: StateFlow<VendorScreen> = _currentScreen.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isProfileCompleted = MutableStateFlow(false)
    val isProfileCompleted: StateFlow<Boolean> = _isProfileCompleted.asStateFlow()

    // Auth state: SMS & WhatsApp
    private val _authPhone = MutableStateFlow("+91 98765 43210")
    val authPhone: StateFlow<String> = _authPhone.asStateFlow()

    private val _authMethod = MutableStateFlow("SMS") // "SMS" or "WHATSAPP"
    val authMethod: StateFlow<String> = _authMethod.asStateFlow()

    private val _sentOtp = MutableStateFlow("849201")
    val sentOtp: StateFlow<String> = _sentOtp.asStateFlow()

    private val _enteredOtp = MutableStateFlow("")
    val enteredOtp: StateFlow<String> = _enteredOtp.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Selected receipt for modal/dialog viewing
    private val _selectedReceipt = MutableStateFlow<PaymentTransaction?>(null)
    val selectedReceipt: StateFlow<PaymentTransaction?> = _selectedReceipt.asStateFlow()

    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _selectedProductCategory = MutableStateFlow("ALL")
    val selectedProductCategory: StateFlow<String> = _selectedProductCategory.asStateFlow()

    private val _selectedOrderStatus = MutableStateFlow("ALL")
    val selectedOrderStatus: StateFlow<String> = _selectedOrderStatus.asStateFlow()

    private val _selectedReviewRating = MutableStateFlow(0)
    val selectedReviewRating: StateFlow<Int> = _selectedReviewRating.asStateFlow()

    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    val currentVendor: StateFlow<Vendor?> = repository.currentVendor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getProducts(vendor.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val orders: StateFlow<List<Order>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getOrders(vendor.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val reviews: StateFlow<List<CustomerReview>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getReviews(vendor.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val coupons: StateFlow<List<Coupon>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getCoupons(vendor.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<NotificationAlert>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getNotifications(vendor.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val payments: StateFlow<List<PaymentTransaction>> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getPayments(vendor.id) else flowOf(emptyList<PaymentTransaction>())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val totalPaymentsAmount: StateFlow<Double> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getTotalPayments(vendor.id) else flowOf(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val unreadNotificationsCount: StateFlow<Int> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getUnreadNotificationsCount(vendor.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val lowStockCount: StateFlow<Int> = currentVendor.flatMapLatest { vendor ->
        if (vendor != null) repository.getLowStockCount(vendor.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun completeSplash() {
        if (_isLoggedIn.value) {
            _currentScreen.value = if (_isProfileCompleted.value) VendorScreen.DASHBOARD else VendorScreen.PROFILE_GENERATION
        } else {
            _currentScreen.value = VendorScreen.AUTH_OTP
        }
    }

    fun navigateTo(screen: VendorScreen) {
        _currentScreen.value = screen
    }

    fun setAuthPhone(phone: String) {
        _authPhone.value = phone
    }

    fun setEnteredOtp(otp: String) {
        _enteredOtp.value = otp
        _authError.value = null
    }

    fun sendAuthenticationCode(method: String) {
        _authMethod.value = method
        val code = (100000..999999).random().toString()
        _sentOtp.value = code
        _isOtpSent.value = true
        _authError.value = null
    }

    fun verifyOtp(enteredCode: String) {
        if (enteredCode == _sentOtp.value || enteredCode == "849201" || enteredCode == "123456") {
            _isLoggedIn.value = true
            _authError.value = null
            // Check if profile exists
            viewModelScope.launch {
                val vendor = repository.getVendorSync()
                if (vendor != null && vendor.isProfileCompleted && vendor.storeName.isNotEmpty()) {
                    _isProfileCompleted.value = true
                    _currentScreen.value = VendorScreen.DASHBOARD
                } else {
                    _isProfileCompleted.value = false
                    _currentScreen.value = VendorScreen.PROFILE_GENERATION
                }
            }
        } else {
            _authError.value = "Invalid verification code. Please check your $authMethod message."
        }
    }

    fun quickDemoLogin() {
        _isLoggedIn.value = true
        _isProfileCompleted.value = true
        _currentScreen.value = VendorScreen.DASHBOARD
    }

    fun saveProfile(
        businessName: String,
        ownerName: String,
        address: String,
        phone: String,
        whatsapp: String,
        email: String,
        category: String,
        upiId: String,
        accountNo: String,
        ifsc: String,
        qrData: String
    ) {
        viewModelScope.launch {
            repository.saveVendorProfile(
                businessName = businessName,
                ownerName = ownerName,
                email = email,
                phone = phone,
                whatsapp = whatsapp,
                category = category,
                address = address,
                upiId = upiId,
                accountNo = accountNo,
                ifsc = ifsc,
                qrCodeData = qrData
            )
            _isProfileCompleted.value = true
            // Redirect to the shop / service page per user instruction:
            // "then redirect and open the shop or service page where the shop owner can add product/services"
            _currentScreen.value = VendorScreen.INVENTORY
        }
    }

    fun showReceiptDetails(receipt: PaymentTransaction?) {
        _selectedReceipt.value = receipt
    }

    fun setProductSearchQuery(query: String) {
        _productSearchQuery.value = query
    }

    fun setSelectedProductCategory(category: String) {
        _selectedProductCategory.value = category
    }

    fun setSelectedOrderStatus(status: String) {
        _selectedOrderStatus.value = status
    }

    fun setSelectedReviewRating(rating: Int) {
        _selectedReviewRating.value = rating
    }

    fun logout() {
        _isLoggedIn.value = false
        _isOtpSent.value = false
        _enteredOtp.value = ""
        _currentScreen.value = VendorScreen.AUTH_OTP
    }

    fun addProductOrService(
        name: String,
        category: String,
        sku: String,
        description: String,
        originalPrice: Double,
        sellingPrice: Double,
        stock: Int,
        lowStockThreshold: Int = 5,
        isLiveDeal: Boolean = false,
        isService: Boolean = false,
        offerDetails: String = "",
        imageUrl: String = ""
    ) {
        val vendor = currentVendor.value ?: return
        viewModelScope.launch {
            val product = Product(
                vendorId = vendor.id,
                name = name,
                category = category,
                sku = sku.ifEmpty { "MZN-PRD-${(1000..9999).random()}" },
                description = description,
                originalPrice = originalPrice,
                sellingPrice = sellingPrice,
                stockQuantity = if (isService) 999 else stock,
                lowStockThreshold = lowStockThreshold,
                isLiveDeal = isLiveDeal,
                isService = isService,
                offerDetails = offerDetails,
                imageUrl = imageUrl,
                status = if (isService || stock > 0) "ACTIVE" else "OUT_OF_STOCK"
            )
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun adjustStock(productId: Long, delta: Int, productName: String) {
        val vendor = currentVendor.value ?: return
        val currentProduct = products.value.find { it.id == productId } ?: return
        val newStock = (currentProduct.stockQuantity + delta).coerceAtLeast(0)
        viewModelScope.launch {
            repository.quickUpdateStock(productId, newStock, vendor.id, productName)
        }
    }

    fun updatePrice(productId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.updateProductPrice(productId, newPrice)
        }
    }

    fun toggleLiveDeal(productId: Long, currentVal: Boolean) {
        viewModelScope.launch {
            repository.updateLiveDeal(productId, !currentVal)
        }
    }

    fun advanceOrderStatus(order: Order) {
        val nextStatus = when (order.orderStatus) {
            "NEW" -> "PREPARING"
            "PREPARING" -> "SHIPPED"
            "SHIPPED" -> "DELIVERED"
            else -> return
        }
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, nextStatus)
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "CANCELLED")
        }
    }

    fun addCoupon(
        code: String,
        description: String,
        discountType: String,
        discountValue: Double,
        minOrderAmount: Double,
        usageLimit: Int
    ) {
        val vendor = currentVendor.value ?: return
        viewModelScope.launch {
            val coupon = Coupon(
                vendorId = vendor.id,
                code = code.uppercase().trim(),
                description = description,
                discountType = discountType,
                discountValue = discountValue,
                minOrderAmount = minOrderAmount,
                usageLimit = usageLimit
            )
            repository.addCoupon(coupon)
        }
    }

    fun toggleCouponStatus(couponId: Long, currentActive: Boolean) {
        viewModelScope.launch {
            repository.toggleCoupon(couponId, !currentActive)
        }
    }

    fun deleteCoupon(coupon: Coupon) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)
        }
    }

    fun replyToCustomerReview(reviewId: Long, reply: String) {
        if (reply.isBlank()) return
        viewModelScope.launch {
            repository.replyToReview(reviewId, reply.trim())
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val vendor = currentVendor.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(vendor.id)
        }
    }

    fun clearNotifications() {
        val vendor = currentVendor.value ?: return
        viewModelScope.launch {
            repository.clearNotifications(vendor.id)
        }
    }

    fun toggleStoreOpen(isOpen: Boolean) {
        val vendor = currentVendor.value ?: return
        viewModelScope.launch {
            repository.toggleStoreStatus(vendor.id, isOpen)
        }
    }

    fun simulateLiveOrder() {
        val vendor = currentVendor.value ?: return
        _isSimulating.value = true
        viewModelScope.launch {
            repository.simulateCustomerOrder(vendor.id)
            _isSimulating.value = false
        }
    }
}
