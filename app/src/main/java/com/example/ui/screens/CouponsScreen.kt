package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Coupon
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznOrange
import com.example.ui.theme.MznRedAccent

@Composable
fun CouponsScreen(
    coupons: List<Coupon>,
    onAddCoupon: (String, String, String, Double, Double, Int) -> Unit,
    onToggleCoupon: (Long, Boolean) -> Unit,
    onDeleteCoupon: (Coupon) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coupons_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                            Icon(Icons.Default.Discount, contentDescription = null, tint = MznOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Marketing & Coupons",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Offer discounts to boost orders & attract live shoppers on MznLive.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MznOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("create_coupon_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Coupon Cards
        if (coupons.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No active coupons. Create one to attract MznLive customers!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(coupons, key = { it.id }) { coupon ->
                CouponCard(
                    coupon = coupon,
                    onToggle = { onToggleCoupon(coupon.id, coupon.isActive) },
                    onDelete = { onDeleteCoupon(coupon) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateCouponDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { code, desc, type, value, minSpend, limit ->
                onAddCoupon(code, desc, type, value, minSpend, limit)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (coupon.timesUsed.toFloat() / coupon.usageLimit).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Code Badge & Active Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dashed coupon pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.border(1.dp, MznOrange, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = MznOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = coupon.code,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MznOrange
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (coupon.isActive) "Active" else "Paused",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (coupon.isActive) MznGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = coupon.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MznGreen
                        ),
                        modifier = Modifier.size(34.dp)
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MznRedAccent, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Value & Description
            val discountText = if (coupon.discountType == "PERCENTAGE") {
                "${coupon.discountValue.toInt()}% OFF"
            } else {
                "$${String.format("%.2f", coupon.discountValue)} FLAT OFF"
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = discountText,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = MznGreen
                )
                if (coupon.minOrderAmount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Min Spend: $${String.format("%.2f", coupon.minOrderAmount)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(coupon.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            // Redemption Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${coupon.timesUsed} Redeemed by Customers",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Limit: ${coupon.usageLimit}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MznOrange,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun CreateCouponDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        code: String,
        description: String,
        discountType: String,
        discountValue: Double,
        minOrderAmount: Double,
        usageLimit: Int
    ) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPercentage by remember { mutableStateOf(true) }
    var valueStr by remember { mutableStateOf("15") }
    var minSpendStr by remember { mutableStateOf("30") }
    var limitStr by remember { mutableStateOf("200") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create MznLive Promo Code") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Coupon Code (e.g. FLASH20)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description for buyers") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Discount Type radio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isPercentage,
                        onClick = { isPercentage = true }
                    )
                    Text("Percentage (%)", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = !isPercentage,
                        onClick = { isPercentage = false }
                    )
                    Text("Flat ($)", fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = valueStr,
                        onValueChange = { valueStr = it },
                        label = { Text(if (isPercentage) "Discount %" else "Discount $") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minSpendStr,
                        onValueChange = { minSpendStr = it },
                        label = { Text("Min Order ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = limitStr,
                    onValueChange = { limitStr = it },
                    label = { Text("Usage Limit (Max Redemptions)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank()) {
                        val valD = valueStr.toDoubleOrNull() ?: 10.0
                        val minS = minSpendStr.toDoubleOrNull() ?: 0.0
                        val lim = limitStr.toIntOrNull() ?: 100
                        val type = if (isPercentage) "PERCENTAGE" else "FLAT"
                        onConfirm(code, description, type, valD, minS, lim)
                    }
                },
                enabled = code.isNotBlank()
            ) {
                Text("Publish Coupon")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
