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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.StockBadge
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznOrange
import com.example.ui.theme.MznRedAccent

@Composable
fun InventoryScreen(
    products: List<Product>,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onAdjustStock: (Long, Int, String) -> Unit,
    onUpdatePrice: (Long, Double) -> Unit,
    onToggleLiveDeal: (Long, Boolean) -> Unit,
    onAddProduct: (
        name: String,
        category: String,
        sku: String,
        description: String,
        originalPrice: Double,
        sellingPrice: Double,
        stock: Int,
        lowStockThreshold: Int,
        isLiveDeal: Boolean,
        isService: Boolean,
        offerDetails: String,
        imageUrl: String
    ) -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var priceEditProduct by remember { mutableStateOf<Product?>(null) }
    var filterType by remember { mutableStateOf("ALL") } // ALL, PRODUCTS, SERVICES

    val categories = listOf("ALL", "Electronics", "Gourmet Food", "Fashion", "Tech Services", "Accessories")

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                product.sku.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "ALL" || product.category.equals(selectedCategory, ignoreCase = true)
        val matchesType = when (filterType) {
            "PRODUCTS" -> !product.isService
            "SERVICES" -> product.isService
            else -> true
        }
        matchesSearch && matchesCategory && matchesType
    }

    Box(modifier = modifier.fillMaxSize().testTag("inventory_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header summary & Search bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Shop & Service Catalog",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${products.size} Items • Instant Sync with MznLive Buyers",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_product_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Product vs Service type switcher chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = filterType == "ALL",
                            onClick = { filterType = "ALL" },
                            label = { Text("All (${products.size})") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MznBluePrimary, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = filterType == "PRODUCTS",
                            onClick = { filterType = "PRODUCTS" },
                            label = { Text("Products (${products.count { !it.isService }})") },
                            leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MznBluePrimary, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = filterType == "SERVICES",
                            onClick = { filterType = "SERVICES" },
                            label = { Text("Services (${products.count { it.isService }})") },
                            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MznBluePrimary, selectedLabelColor = Color.White)
                        )
                    }

                    // Search textfield
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_search_input"),
                        placeholder = { Text("Search products, services, or offers...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { onCategorySelect(cat) },
                                label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MznBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Product / Service Cards List
            if (filteredProducts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No items found matching your filters.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductOrServiceCard(
                        product = product,
                        onIncrementStock = { onAdjustStock(product.id, 1, product.name) },
                        onDecrementStock = { onAdjustStock(product.id, -1, product.name) },
                        onQuickPriceClick = { priceEditProduct = product },
                        onToggleLiveDeal = { onToggleLiveDeal(product.id, product.isLiveDeal) },
                        onEdit = { editingProduct = product },
                        onDelete = { onDeleteProduct(product) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Add Product / Service Dialog
    if (showAddDialog) {
        AddEditProductOrServiceDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, sku, desc, origPrice, sellPrice, stock, thresh, live, isServ, offer, img ->
                onAddProduct(name, cat, sku, desc, origPrice, sellPrice, stock, thresh, live, isServ, offer, img)
                showAddDialog = false
            }
        )
    }

    // Edit Product Dialog
    editingProduct?.let { prod ->
        AddEditProductOrServiceDialog(
            product = prod,
            onDismiss = { editingProduct = null },
            onConfirm = { name, cat, sku, desc, origPrice, sellPrice, stock, thresh, live, isServ, offer, img ->
                onEditProduct(
                    prod.copy(
                        name = name,
                        category = cat,
                        sku = sku,
                        description = desc,
                        originalPrice = origPrice,
                        sellingPrice = sellPrice,
                        stockQuantity = stock,
                        lowStockThreshold = thresh,
                        isLiveDeal = live,
                        isService = isServ,
                        offerDetails = offer,
                        imageUrl = img,
                        status = if (isServ || stock > 0) "ACTIVE" else "OUT_OF_STOCK"
                    )
                )
                editingProduct = null
            }
        )
    }

    // Quick Price Dialog
    priceEditProduct?.let { prod ->
        var newPriceText by remember { mutableStateOf(prod.sellingPrice.toString()) }
        AlertDialog(
            onDismissRequest = { priceEditProduct = null },
            title = { Text("Update Price for ${prod.name}") },
            text = {
                Column {
                    Text("Original Price: $${String.format("%.2f", prod.originalPrice)}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPriceText,
                        onValueChange = { newPriceText = it },
                        label = { Text("Selling Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newPriceText.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            onUpdatePrice(prod.id, parsed)
                        }
                        priceEditProduct = null
                    }
                ) {
                    Text("Save Price")
                }
            },
            dismissButton = {
                TextButton(onClick = { priceEditProduct = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProductOrServiceCard(
    product: Product,
    onIncrementStock: () -> Unit,
    onDecrementStock: () -> Unit,
    onQuickPriceClick: () -> Unit,
    onToggleLiveDeal: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Type pill (PRODUCT vs SERVICE), Category, Live Deal badge, Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.isService) MznGold.copy(alpha = 0.2f) else MznBluePrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (product.isService) "SERVICE" else "PRODUCT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (product.isService) MznGold else MznBlueLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = product.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (product.isLiveDeal) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MznOrange.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = MznOrange, modifier = Modifier.size(12.dp))
                                Text("Live Deal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MznOrange)
                            }
                        }
                    }
                }

                if (!product.isService) {
                    StockBadge(quantity = product.stockQuantity, threshold = product.lowStockThreshold)
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MznGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Available to Book",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MznGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Description
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (product.description.isNotBlank()) {
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            // Special Offer details banner if vendor added an offer!
            if (product.offerDetails.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MznOrange.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MznOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Offer: ${product.offerDetails}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MznOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Stock Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price & Edit affordance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onQuickPriceClick() }
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.sellingPrice)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MznGreen
                    )
                    if (product.originalPrice > product.sellingPrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$${String.format("%.2f", product.originalPrice)}",
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "-${product.discountPercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MznRedAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Price",
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick Stock +/- Stepper (only for physical products)
                if (!product.isService) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = onDecrementStock,
                            modifier = Modifier.size(28.dp),
                            enabled = product.stockQuantity > 0
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Stock", modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = "${product.stockQuantity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = onIncrementStock,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Stock", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Live Deal toggle, Edit full, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleLiveDeal() }
                ) {
                    Switch(
                        checked = product.isLiveDeal,
                        onCheckedChange = { onToggleLiveDeal() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MznOrange
                        ),
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Broadcast on MznLive",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (product.isLiveDeal) MznOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Product Details", tint = MznBlueLight, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = MznRedAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditProductOrServiceDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        category: String,
        sku: String,
        description: String,
        originalPrice: Double,
        sellingPrice: Double,
        stock: Int,
        lowStockThreshold: Int,
        isLiveDeal: Boolean,
        isService: Boolean,
        offerDetails: String,
        imageUrl: String
    ) -> Unit
) {
    var isService by remember { mutableStateOf(product?.isService ?: false) }
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Electronics") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var originalPriceStr by remember { mutableStateOf(product?.originalPrice?.toString() ?: "50.0") }
    var sellingPriceStr by remember { mutableStateOf(product?.sellingPrice?.toString() ?: "39.99") }
    var stockStr by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "15") }
    var thresholdStr by remember { mutableStateOf(product?.lowStockThreshold?.toString() ?: "5") }
    var offerDetails by remember { mutableStateOf(product?.offerDetails ?: "") }
    var isLiveDeal by remember { mutableStateOf(product?.isLiveDeal ?: false) }
    var hasImageUploaded by remember { mutableStateOf(product?.imageUrl?.isNotEmpty() ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add Product or Service" else "Edit Item Details") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Type selector: Product vs Service
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isService) MznBluePrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isService = false }
                                .border(1.dp, if (!isService) MznBluePrimary else Color.Transparent, RoundedCornerShape(8.dp))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = !isService, onClick = { isService = false })
                                Text("Physical Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isService) MznGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isService = true }
                                .border(1.dp, if (isService) MznGold else Color.Transparent, RoundedCornerShape(8.dp))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = isService, onClick = { isService = true })
                                Text("Service / Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isService) "Service Title *" else "Product Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (e.g. Electronics, Tech Services)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Image upload affordance
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasImageUploaded = true }
                            .border(1.dp, if (hasImageUploaded) MznGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = if (hasImageUploaded) MznGreen else MznBlueLight)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (hasImageUploaded) "✓ Image Uploaded & Verified" else "Upload Product / Service Image",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (hasImageUploaded) MznGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Text("Tap to attach image from gallery or camera", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isService) "Service Details & Deliverables" else "Product Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = originalPriceStr,
                            onValueChange = { originalPriceStr = it },
                            label = { Text("Original ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = sellingPriceStr,
                            onValueChange = { sellingPriceStr = it },
                            label = { Text("Selling ($) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Discount and Offer details input
                item {
                    OutlinedTextField(
                        value = offerDetails,
                        onValueChange = { offerDetails = it },
                        label = { Text("Special Offer / Discount Details") },
                        placeholder = { Text("e.g. 'Flat 20% Off', 'Buy 1 Get 1', 'Free Setup'") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                if (!isService) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = stockStr,
                                onValueChange = { stockStr = it },
                                label = { Text("Stock Quantity *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = thresholdStr,
                                onValueChange = { thresholdStr = it },
                                label = { Text("Alert Thresh") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Broadcast on MznLive Stream", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Switch(
                            checked = isLiveDeal,
                            onCheckedChange = { isLiveDeal = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = MznOrange)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val orig = originalPriceStr.toDoubleOrNull() ?: 0.0
                        val sell = sellingPriceStr.toDoubleOrNull() ?: 0.0
                        val stk = stockStr.toIntOrNull() ?: 0
                        val thresh = thresholdStr.toIntOrNull() ?: 5
                        val img = if (hasImageUploaded) "uploaded_img" else ""
                        onConfirm(name, category, sku, description, orig, sell, stk, thresh, isLiveDeal, isService, offerDetails, img)
                    }
                },
                enabled = name.isNotBlank() && sellingPriceStr.isNotBlank()
            ) {
                Text(if (product == null) "Add to Store" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
