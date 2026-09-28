package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Vendor
import com.example.ui.components.LivePillBadge
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznRedAccent

@Composable
fun ProfileGenerationScreen(
    vendor: Vendor?,
    onSaveProfile: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf(vendor?.storeName ?: "Mzn Prime Superstore") }
    var ownerName by remember { mutableStateOf(vendor?.name ?: "Zaid Khan") }
    var address by remember { mutableStateOf(vendor?.storeAddress ?: "Shop 14, Tech Plaza, Downtown Market") }
    var phone by remember { mutableStateOf(vendor?.phone ?: "+91 98765 43210") }
    var whatsapp by remember { mutableStateOf(vendor?.whatsappNumber ?: "+91 98765 43210") }
    var email by remember { mutableStateOf(vendor?.email ?: "vendor@mznlive.com") }
    var category by remember { mutableStateOf(vendor?.category ?: "Electronics & Gadgets") }

    // Payment configuration option: 0: UPI ID, 1: Bank Account, 2: Upload QR
    var paymentOption by remember { mutableIntStateOf(0) }
    var upiId by remember { mutableStateOf(vendor?.upiId ?: "mznprime@upi") }
    var accountNo by remember { mutableStateOf(vendor?.bankAccountNumber ?: "9182740192841") }
    var ifsc by remember { mutableStateOf(vendor?.bankIfsc ?: "HDFC0001824") }
    var isQrUploaded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Electronics & Gadgets",
        "Fresh Groceries & Organic",
        "Fashion & Streetwear",
        "Restaurant & Takeaway",
        "Home & Living",
        "Beauty & Wellness",
        "Repair & Tech Services"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_generation_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF070E24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mznlive_vendor_logo),
                            contentDescription = "MznLive Vendors Logo",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Store Profile Setup",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            LivePillBadge()
                        }
                        Text(
                            text = "Set up your store details & payment system to start receiving orders on MznLive.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section 1: Business & Owner Details
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = MznBlueLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Business Information", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business / Store Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner / Manager Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Physical Store Address / Landmark *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Business Category *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MznBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Contact Details (Phone, WhatsApp, Email)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MznGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contact & Chat Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Customer Support Phone *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Chat Number (For buyer queries & receipts) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Business Settlement Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Section 3: App Payment System (UPI, Bank Account, QR Code)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = MznGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Payment System Setup", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Connect your payout mode. A dedicated transaction database will log all payments, and digital acknowledgement receipts will be automatically delivered to customers and your store.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Payment Method selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (paymentOption == 0) MznBluePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentOption = 0 }
                                .border(1.dp, if (paymentOption == 0) MznBluePrimary else Color.Transparent, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("UPI ID", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (paymentOption == 0) MznBlueLight else MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (paymentOption == 1) MznBluePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1.2f)
                                .clickable { paymentOption = 1 }
                                .border(1.dp, if (paymentOption == 1) MznBluePrimary else Color.Transparent, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Bank Account", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (paymentOption == 1) MznBlueLight else MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (paymentOption == 2) MznBluePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentOption = 2 }
                                .border(1.dp, if (paymentOption == 2) MznBluePrimary else Color.Transparent, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("QR Code", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (paymentOption == 2) MznBlueLight else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    when (paymentOption) {
                        0 -> {
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("Enter Merchant UPI ID (e.g. store@upi)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = accountNo,
                                    onValueChange = { accountNo = it },
                                    label = { Text("Bank Account Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = ifsc,
                                    onValueChange = { ifsc = it.uppercase() },
                                    label = { Text("Bank IFSC Code") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                        2 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isQrUploaded = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isQrUploaded) "✓ QR Code Uploaded / Active" else "Upload Shop QR Code Image", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Interactive Merchant Payment QR Display Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Merchant Instant Payment QR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MznGold)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Simulated QR Code Graphic with MznLive badge in center
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "Payment QR Code",
                                    tint = Color.Black,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF070E24)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.mznlive_vendor_logo),
                                        contentDescription = "MznLive Center",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("UPI: $upiId", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Auto-generates Customer & Merchant Receipt", fontSize = 10.sp, color = MznGreen)
                        }
                    }
                }
            }
        }

        // Save & Redirect Button
        item {
            Button(
                onClick = {
                    onSaveProfile(
                        businessName,
                        ownerName,
                        address,
                        phone,
                        whatsapp,
                        email,
                        category,
                        upiId,
                        accountNo,
                        ifsc,
                        "upi://pay?pa=$upiId&pn=${businessName.replace(" ", "%20")}"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_profile_and_open_shop_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                shape = RoundedCornerShape(14.dp),
                enabled = businessName.isNotBlank() && ownerName.isNotBlank()
            ) {
                Text("Save Profile & Open Shop/Services", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
