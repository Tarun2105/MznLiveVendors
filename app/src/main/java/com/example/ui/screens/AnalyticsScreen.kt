package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.ui.components.SalesTrendCanvas
import com.example.ui.components.StatCard
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznOrange
import com.example.ui.theme.MznRedAccent

@Composable
fun AnalyticsScreen(
    vendor: Vendor?,
    products: List<Product>,
    orders: List<Order>,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("7 Days") }
    val timeframes = listOf("Today", "7 Days", "30 Days", "All Time")

    val completedOrders = orders.filter { it.orderStatus != "CANCELLED" }
    val totalRevenue = completedOrders.sumOf { it.finalAmount }
    val aov = if (completedOrders.isNotEmpty()) totalRevenue / completedOrders.size else 0.0

    // Category breakdown
    val categoryCounts = products.groupBy { it.category }
    val totalProducts = products.size.coerceAtLeast(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Timeframe selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Sales & Performance Analytics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(timeframes) { tf ->
                        FilterChip(
                            selected = selectedTimeframe == tf,
                            onClick = { selectedTimeframe = tf },
                            label = { Text(tf, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MznBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Sales",
                        value = "$${String.format("%.2f", totalRevenue)}",
                        subtitle = "↑ 24.2% Growth",
                        icon = Icons.Default.AttachMoney,
                        accentColor = MznGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Order Value",
                        value = "$${String.format("%.2f", aov)}",
                        subtitle = "Across ${completedOrders.size} orders",
                        icon = Icons.Default.ShoppingBag,
                        accentColor = MznBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Fulfillment Rate",
                        value = "98.5%",
                        subtitle = "Fast dispatch on MznLive",
                        icon = Icons.Default.Bolt,
                        accentColor = MznGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Store Rating",
                        value = "${vendor?.rating ?: 4.8} ★",
                        subtitle = "${vendor?.totalSalesCount ?: 1420}+ items sold",
                        icon = Icons.Default.Star,
                        accentColor = MznOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sales Revenue Curve Canvas
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Revenue Velocity ($selectedTimeframe)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SalesTrendCanvas(
                        dataPoints = listOf(40f, 65f, 52f, 88f, 74f, 110f, 135f, 120f, 160f, 145f, 195f),
                        lineColor = MznGreen
                    )
                }
            }
        }

        // Category Share Distribution
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Inventory Share by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    categoryCounts.forEach { (category, prods) ->
                        val share = prods.size.toFloat() / totalProducts
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(category, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("${(share * 100).toInt()}% (${prods.size})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { share },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MznBluePrimary,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }

        // Top Selling Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Top Performing Products",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    products.sortedByDescending { it.reviewsCount }.take(4).forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (index == 0) MznGold else MznBluePrimary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${index + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (index == 0) Color.Black else MznBlueLight
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${product.reviewsCount} customer orders on MznLive",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "$${String.format("%.2f", product.sellingPrice)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MznGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
