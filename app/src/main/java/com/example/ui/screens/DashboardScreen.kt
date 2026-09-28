package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.ui.components.LivePillBadge
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SalesTrendCanvas
import com.example.ui.components.StatCard
import com.example.ui.components.StockBadge
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznOrange
import com.example.ui.theme.MznRedAccent
import com.example.ui.viewmodel.VendorScreen

@Composable
fun DashboardScreen(
    vendor: Vendor?,
    products: List<Product>,
    orders: List<Order>,
    lowStockCount: Int,
    isSimulating: Boolean,
    onNavigate: (VendorScreen) -> Unit,
    onSimulateLiveOrder: () -> Unit,
    onRestockQuick: (Long, Int, String) -> Unit,
    onToggleStoreOpen: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRevenue = orders.filter { it.orderStatus != "CANCELLED" }.sumOf { it.finalAmount }
    val newOrders = orders.filter { it.orderStatus == "NEW" }
    val pendingOrdersCount = orders.count { it.orderStatus in listOf("NEW", "PREPARING") }
    val lowStockItems = products.filter { it.stockQuantity <= it.lowStockThreshold }

    // Sample hourly sales points for canvas
    val salesTrendPoints = listOf(15f, 32f, 28f, 55f, 42f, 78f, 65f, 95f, 110f, 85f, 125f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Store Header & Live Status Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vendor?.storeName ?: "My Store",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (vendor?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Vendor",
                                    tint = MznBlueLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (vendor?.isOpen == true) "Accepting orders on MznLive App" else "Store temporarily paused",
                            fontSize = 12.sp,
                            color = if (vendor?.isOpen == true) MznGreen else MznRedAccent
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (vendor?.isOpen == true) "OPEN" else "OFFLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (vendor?.isOpen == true) MznGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = vendor?.isOpen ?: true,
                            onCheckedChange = { onToggleStoreOpen(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MznGreen
                            ),
                            modifier = Modifier.testTag("store_open_switch")
                        )
                    }
                }
            }
        }

        // 2. Real-time KPI Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Gross Revenue",
                        value = "$${String.format("%.2f", totalRevenue)}",
                        subtitle = "↑ 18.4% vs last week",
                        icon = Icons.Default.AttachMoney,
                        accentColor = MznGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending Orders",
                        value = pendingOrdersCount.toString(),
                        subtitle = if (newOrders.isNotEmpty()) "${newOrders.size} Need action" else "All up to date",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = if (newOrders.isNotEmpty()) MznRedAccent else MznBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Low Stock Alerts",
                        value = lowStockCount.toString(),
                        subtitle = if (lowStockCount > 0) "Urgent restock" else "Optimal inventory",
                        icon = Icons.Default.Warning,
                        accentColor = if (lowStockCount > 0) MznGold else MznBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Catalog",
                        value = products.count { it.status == "ACTIVE" }.toString(),
                        subtitle = "${products.size} total items",
                        icon = Icons.Default.Inventory2,
                        accentColor = MznBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Companion Action: Simulate Customer Order & Real-time Live Alert
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MznBluePrimary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = MznGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MznLive Companion Integration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Simulate a live customer purchase on MznLive to test push alerts and real-time order queue.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onSimulateLiveOrder,
                        enabled = !isSimulating,
                        colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("simulate_customer_order_btn")
                    ) {
                        if (isSimulating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Test Order", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Sales Trends Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Real-Time Sales Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Today's sales velocity curve",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        LivePillBadge()
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    SalesTrendCanvas(dataPoints = salesTrendPoints)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("8 AM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("12 PM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("4 PM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Now (Live)", fontSize = 11.sp, color = MznBlueLight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Urgent Low Stock Warning Carousel
        if (lowStockItems.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MznGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Low Stock Warnings (${lowStockItems.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MznBlueLight,
                            modifier = Modifier.clickable { onNavigate(VendorScreen.INVENTORY) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(lowStockItems) { product ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StockBadge(
                                            quantity = product.stockQuantity,
                                            threshold = product.lowStockThreshold
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", product.sellingPrice)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onRestockQuick(product.id, 10, product.name) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .testTag("restock_btn_${product.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MznGold),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = "+10 Quick Restock",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Recent Orders Preview
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Incoming Orders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage All (${orders.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MznBlueLight,
                        modifier = Modifier.clickable { onNavigate(VendorScreen.ORDERS) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (orders.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No orders yet. Tap 'Test Order' above to simulate a customer order!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        orders.take(3).forEach { order ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(VendorScreen.ORDERS) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "#${order.orderNumber}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            OrderStatusBadge(status = order.orderStatus)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = order.customerName,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = order.itemsSummary,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = "$${String.format("%.2f", order.finalAmount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MznGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Quick Shortcuts
        item {
            Text(
                text = "Vendor Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickShortcutButton(
                    title = "New Product",
                    icon = Icons.Default.Add,
                    color = MznBluePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(VendorScreen.INVENTORY) }
                )
                QuickShortcutButton(
                    title = "Receipts",
                    icon = Icons.Default.ReceiptLong,
                    color = MznGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(VendorScreen.PAYMENTS) }
                )
                QuickShortcutButton(
                    title = "Offers",
                    icon = Icons.Default.Discount,
                    color = MznOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(VendorScreen.MARKETING) }
                )
                QuickShortcutButton(
                    title = "Analytics",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = MznBlueLight,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(VendorScreen.ANALYTICS) }
                )
            }
        }
    }
}

@Composable
private fun QuickShortcutButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
