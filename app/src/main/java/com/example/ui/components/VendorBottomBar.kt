package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznRedAccent
import com.example.ui.viewmodel.VendorScreen

@Composable
fun VendorBottomBar(
    currentScreen: VendorScreen,
    newOrdersCount: Int,
    lowStockCount: Int,
    onNavigate: (VendorScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vendor_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // 1. Dashboard
        NavigationBarItem(
            selected = currentScreen == VendorScreen.DASHBOARD,
            onClick = { onNavigate(VendorScreen.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Overview", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )

        // 2. Shop & Services
        NavigationBarItem(
            selected = currentScreen == VendorScreen.INVENTORY,
            onClick = { onNavigate(VendorScreen.INVENTORY) },
            icon = {
                BadgedBox(
                    badge = {
                        if (lowStockCount > 0) {
                            Badge(containerColor = MznGold, contentColor = Color.Black) {
                                Text("$lowStockCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = "Shop & Services"
                    )
                }
            },
            label = { Text("Shop/Service", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )

        // 3. Orders
        NavigationBarItem(
            selected = currentScreen == VendorScreen.ORDERS,
            onClick = { onNavigate(VendorScreen.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (newOrdersCount > 0) {
                            Badge(containerColor = MznRedAccent, contentColor = Color.White) {
                                Text("$newOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Orders"
                    )
                }
            },
            label = { Text("Orders", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )

        // 4. Payments & Receipts
        NavigationBarItem(
            selected = currentScreen == VendorScreen.PAYMENTS,
            onClick = { onNavigate(VendorScreen.PAYMENTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = "Payments & Receipts"
                )
            },
            label = { Text("Payments", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )

        // 5. Offers & Coupons
        NavigationBarItem(
            selected = currentScreen == VendorScreen.MARKETING,
            onClick = { onNavigate(VendorScreen.MARKETING) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Discount,
                    contentDescription = "Offers"
                )
            },
            label = { Text("Offers", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )

        // 6. Reviews & Feedback
        NavigationBarItem(
            selected = currentScreen == VendorScreen.FEEDBACK,
            onClick = { onNavigate(VendorScreen.FEEDBACK) },
            icon = {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = "Feedback"
                )
            },
            label = { Text("Reviews", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MznBlueLight,
                selectedTextColor = MznBlueLight,
                indicatorColor = MznBluePrimary.copy(alpha = 0.2f)
            )
        )
    }
}
