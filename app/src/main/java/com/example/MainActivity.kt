package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TopVendorAppBar
import com.example.ui.components.VendorBottomBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CouponsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeedbackScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PaymentsReceiptScreen
import com.example.ui.screens.ProfileGenerationScreen
import com.example.ui.screens.SmsWhatsAppAuthScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StoreProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VendorScreen
import com.example.ui.viewmodel.VendorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VendorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: VendorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isProfileCompleted by viewModel.isProfileCompleted.collectAsStateWithLifecycle()

    val currentVendor by viewModel.currentVendor.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()

    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()

    val authPhone by viewModel.authPhone.collectAsStateWithLifecycle()
    val authMethod by viewModel.authMethod.collectAsStateWithLifecycle()
    val isOtpSent by viewModel.isOtpSent.collectAsStateWithLifecycle()
    val sentOtp by viewModel.sentOtp.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    val searchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedProductCategory.collectAsStateWithLifecycle()
    val selectedOrderStatus by viewModel.selectedOrderStatus.collectAsStateWithLifecycle()
    val selectedReviewRating by viewModel.selectedReviewRating.collectAsStateWithLifecycle()
    val isSimulating by viewModel.isSimulating.collectAsStateWithLifecycle()

    // Request Notification Permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handled gracefully
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Handle back button on sub-screens
    BackHandler(enabled = isLoggedIn && currentScreen != VendorScreen.DASHBOARD && currentScreen != VendorScreen.SPLASH) {
        viewModel.navigateTo(VendorScreen.DASHBOARD)
    }

    // 1. Splash Screen
    if (currentScreen == VendorScreen.SPLASH) {
        SplashScreen(
            onContinue = { viewModel.completeSplash() }
        )
        return
    }

    // 2. First-time Login with SMS and WhatsApp Chat Authentication
    if (currentScreen == VendorScreen.AUTH_OTP || !isLoggedIn) {
        SmsWhatsAppAuthScreen(
            phone = authPhone,
            authMethod = authMethod,
            isOtpSent = isOtpSent,
            sentOtp = sentOtp,
            authError = authError,
            onPhoneChange = { viewModel.setAuthPhone(it) },
            onSendCode = { viewModel.sendAuthenticationCode(it) },
            onVerifyOtp = { viewModel.verifyOtp(it) },
            onQuickDemo = { viewModel.quickDemoLogin() }
        )
        return
    }

    // 3. Profile Generation Page (Business, Owner, Contact, Category, UPI & QR Code)
    if (currentScreen == VendorScreen.PROFILE_GENERATION || !isProfileCompleted) {
        ProfileGenerationScreen(
            vendor = currentVendor,
            onSaveProfile = { bName, oName, addr, ph, wa, em, cat, upi, acc, ifsc, qr ->
                viewModel.saveProfile(bName, oName, addr, ph, wa, em, cat, upi, acc, ifsc, qr)
            }
        )
        return
    }

    // 4. Main Authenticated Store Scaffold
    Scaffold(
        topBar = {
            TopVendorAppBar(
                vendor = currentVendor,
                currentScreen = currentScreen,
                unreadAlertsCount = unreadNotificationsCount,
                onNotificationClick = { viewModel.navigateTo(VendorScreen.NOTIFICATIONS) },
                onProfileClick = { viewModel.navigateTo(VendorScreen.STORE_PROFILE) },
                onToggleStoreOpen = { viewModel.toggleStoreOpen(it) }
            )
        },
        bottomBar = {
            VendorBottomBar(
                currentScreen = currentScreen,
                newOrdersCount = orders.count { it.orderStatus == "NEW" },
                lowStockCount = lowStockCount,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                VendorScreen.DASHBOARD -> {
                    DashboardScreen(
                        vendor = currentVendor,
                        products = products,
                        orders = orders,
                        lowStockCount = lowStockCount,
                        isSimulating = isSimulating,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSimulateLiveOrder = { viewModel.simulateLiveOrder() },
                        onRestockQuick = { id, qty, name -> viewModel.adjustStock(id, qty, name) },
                        onToggleStoreOpen = { viewModel.toggleStoreOpen(it) }
                    )
                }

                VendorScreen.INVENTORY -> {
                    InventoryScreen(
                        products = products,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setProductSearchQuery(it) },
                        onCategorySelect = { viewModel.setSelectedProductCategory(it) },
                        onAdjustStock = { id, delta, name -> viewModel.adjustStock(id, delta, name) },
                        onUpdatePrice = { id, price -> viewModel.updatePrice(id, price) },
                        onToggleLiveDeal = { id, current -> viewModel.toggleLiveDeal(id, current) },
                        onAddProduct = { name, cat, sku, desc, orig, sell, stock, thresh, live, isServ, offer, img ->
                            viewModel.addProductOrService(name, cat, sku, desc, orig, sell, stock, thresh, live, isServ, offer, img)
                        },
                        onEditProduct = { viewModel.updateProduct(it) },
                        onDeleteProduct = { viewModel.deleteProduct(it) }
                    )
                }

                VendorScreen.ORDERS -> {
                    OrdersScreen(
                        orders = orders,
                        selectedStatus = selectedOrderStatus,
                        isSimulating = isSimulating,
                        onStatusSelect = { viewModel.setSelectedOrderStatus(it) },
                        onAdvanceOrderStatus = { viewModel.advanceOrderStatus(it) },
                        onCancelOrder = { viewModel.cancelOrder(it) },
                        onSimulateOrder = { viewModel.simulateLiveOrder() }
                    )
                }

                VendorScreen.PAYMENTS -> {
                    PaymentsReceiptScreen(
                        vendor = currentVendor,
                        payments = payments,
                        selectedReceipt = selectedReceipt,
                        onSelectReceipt = { viewModel.showReceiptDetails(it) }
                    )
                }

                VendorScreen.ANALYTICS -> {
                    AnalyticsScreen(
                        vendor = currentVendor,
                        products = products,
                        orders = orders
                    )
                }

                VendorScreen.FEEDBACK -> {
                    FeedbackScreen(
                        reviews = reviews,
                        selectedRating = selectedReviewRating,
                        onRatingFilter = { viewModel.setSelectedReviewRating(it) },
                        onReplyToReview = { id, reply -> viewModel.replyToCustomerReview(id, reply) }
                    )
                }

                VendorScreen.MARKETING -> {
                    CouponsScreen(
                        coupons = coupons,
                        onAddCoupon = { code, desc, type, value, minSpend, limit ->
                            viewModel.addCoupon(code, desc, type, value, minSpend, limit)
                        },
                        onToggleCoupon = { id, active -> viewModel.toggleCouponStatus(id, active) },
                        onDeleteCoupon = { viewModel.deleteCoupon(it) }
                    )
                }

                VendorScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAsRead = { viewModel.markNotificationRead(it) },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        onClearAll = { viewModel.clearNotifications() }
                    )
                }

                VendorScreen.STORE_PROFILE -> {
                    StoreProfileScreen(
                        vendor = currentVendor,
                        onToggleStoreOpen = { viewModel.toggleStoreOpen(it) },
                        onLogout = { viewModel.logout() }
                    )
                }

                else -> {}
            }
        }
    }
}
