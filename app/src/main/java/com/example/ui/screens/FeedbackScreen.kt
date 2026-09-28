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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerReview
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznRedAccent

@Composable
fun FeedbackScreen(
    reviews: List<CustomerReview>,
    selectedRating: Int,
    onRatingFilter: (Int) -> Unit,
    onReplyToReview: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var replyingToReview by remember { mutableStateOf<CustomerReview?>(null) }
    var replyText by remember { mutableStateOf("") }

    val filteredReviews = if (selectedRating == 0) {
        reviews
    } else {
        reviews.filter { it.rating == selectedRating }
    }

    val avgRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 5.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feedback_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overview card
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
                    Column {
                        Text(
                            text = "Customer Satisfaction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Verified reviews from MznLive customers",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.1f", avgRating),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row {
                                    repeat(5) { i ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (i < avgRating.toInt()) MznGold else Color.Gray.copy(alpha = 0.3f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${reviews.size} total reviews",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MznGreen.copy(alpha = 0.15f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Top Rated", fontWeight = FontWeight.Bold, color = MznGreen, fontSize = 12.sp)
                            Text("Vendor", fontWeight = FontWeight.Black, color = MznGreen, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Filter chips (All, 5, 4, 3, 2, 1)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedRating == 0,
                        onClick = { onRatingFilter(0) },
                        label = { Text("All Stars (${reviews.size})") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MznBluePrimary, selectedLabelColor = Color.White)
                    )
                }
                items(listOf(5, 4, 3, 2, 1)) { star ->
                    val count = reviews.count { it.rating == star }
                    FilterChip(
                        selected = selectedRating == star,
                        onClick = { onRatingFilter(star) },
                        label = { Text("$star ★ ($count)") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MznBluePrimary, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        // Reviews list
        if (filteredReviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No reviews found for this rating.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredReviews, key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    onOpenReply = {
                        replyingToReview = review
                        replyText = review.vendorReply ?: ""
                    }
                )
            }
        }
    }

    // Reply Dialog
    replyingToReview?.let { rev ->
        AlertDialog(
            onDismissRequest = { replyingToReview = null },
            title = { Text("Reply to ${rev.customerName}") },
            text = {
                Column {
                    Text("Customer: \"${rev.comment}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Official Store Response") },
                        placeholder = { Text("Thank you for your feedback!...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReplyToReview(rev.id, replyText)
                        replyingToReview = null
                    },
                    enabled = replyText.isNotBlank()
                ) {
                    Text("Post Reply")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyingToReview = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ReviewCard(
    review: CustomerReview,
    onOpenReply: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Customer name, Verified, Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MznBluePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.customerName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = MznBlueLight,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(review.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (review.isVerifiedPurchase) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MznGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Verified MznLive Buyer", fontSize = 10.sp, color = MznGreen, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i < review.rating) MznGold else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product target
            Text(
                text = "Product: ${review.productName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MznBlueLight
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Comment
            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vendor Reply
            if (review.vendorReply != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Your Store Response:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MznBlueLight)
                            Text(
                                text = "Edit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MznBlueLight,
                                modifier = Modifier.clickable { onOpenReply() }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(review.vendorReply, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                Button(
                    onClick = onOpenReply,
                    modifier = Modifier.align(Alignment.End).height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Reply, contentDescription = null, tint = MznBlueLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reply to Buyer", fontSize = 11.sp, color = MznBlueLight, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
