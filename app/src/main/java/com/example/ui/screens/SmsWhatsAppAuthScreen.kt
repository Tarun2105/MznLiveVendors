package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LivePillBadge
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznGreen
import com.example.ui.theme.MznRedAccent

@Composable
fun SmsWhatsAppAuthScreen(
    phone: String,
    authMethod: String,
    isOtpSent: Boolean,
    sentOtp: String,
    authError: String?,
    onPhoneChange: (String) -> Unit,
    onSendCode: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onQuickDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sms_whatsapp_auth_screen")
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            // Emblem Logo
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF070E24)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mznlive_vendor_logo),
                    contentDescription = "MznLive Vendors Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MznLive",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Vendors",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = MznRedAccent
                )
                Spacer(modifier = Modifier.width(6.dp))
                LivePillBadge()
            }

            Text(
                text = "Partner Verification & Onboarding",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Strictly required note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MznBluePrimary.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MznBlueLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SMS and WhatsApp chat authentication is strictly required for all shop owners before opening their store.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Input
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Registered Mobile Number") },
                placeholder = { Text("+91 98765 43210") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = MznBlueLight)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_phone_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                enabled = !isOtpSent
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!isOtpSent) {
                // Dual Authentication Channel Buttons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onSendCode("SMS") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_sms_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authenticate via SMS OTP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = { onSendCode("WHATSAPP") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_whatsapp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authenticate via WhatsApp Chat", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            } else {
                // Incoming verification simulation alert banner
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (authMethod == "WHATSAPP") Color(0xFF1E3A2B) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (authMethod == "WHATSAPP") Color(0xFF25D366) else MznBlueLight,
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (authMethod == "WHATSAPP") Icons.Default.Chat else Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = if (authMethod == "WHATSAPP") Color(0xFF25D366) else MznBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (authMethod == "WHATSAPP") "Incoming WhatsApp Security Message" else "Incoming SMS from MznLive",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (authMethod == "WHATSAPP") Color(0xFF25D366) else MznBlueLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your MznLive Vendor OTP verification code is: $sentOtp. Do not share this code.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = {
                                    otpInput = sentOtp
                                    onVerifyOtp(sentOtp)
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text("⚡ 1-Tap Auto-fill & Verify Code", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MznGold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OTP Input
                OutlinedTextField(
                    value = otpInput,
                    onValueChange = { if (it.length <= 6) otpInput = it },
                    label = { Text("Enter 6-Digit Verification Code") },
                    placeholder = { Text("849201") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_code_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (authError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = authError, color = MznRedAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onVerifyOtp(otpInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verify_otp_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = otpInput.length >= 6
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verify & Continue to Profile Setup", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onSendCode(if (authMethod == "SMS") "WHATSAPP" else "SMS") }) {
                        Text(
                            text = if (authMethod == "SMS") "Switch to WhatsApp Chat" else "Switch to SMS OTP",
                            fontSize = 12.sp,
                            color = MznBlueLight
                        )
                    }

                    TextButton(onClick = { onSendCode(authMethod) }) {
                        Text("Resend Code", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick bypass demo for testing
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Evaluation Shortcut:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onQuickDemo) {
                        Text("Skip to Demo Store", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MznGold)
                    }
                }
            }
        }
    }
}
