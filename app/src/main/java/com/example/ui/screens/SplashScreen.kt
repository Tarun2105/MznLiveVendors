package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LivePillBadge
import com.example.ui.theme.MznBlueDark
import com.example.ui.theme.MznBlueLight
import com.example.ui.theme.MznBluePrimary
import com.example.ui.theme.MznGold
import com.example.ui.theme.MznNavyDark
import com.example.ui.theme.MznNavySurface
import com.example.ui.theme.MznRedAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    val transition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseGlow by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, animationSpec = tween(500))
        delay(1400)
        onContinue()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MznNavyDark,
                        Color(0xFF091436),
                        MznNavySurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Glowing circular backdrop
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseGlow)
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MznBluePrimary.copy(alpha = 0.4f),
                                    MznBlueDark.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 3D MznLive Vendors Logo
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .clip(CircleShape)
                        .background(Color(0xFF070E24)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mznlive_vendor_logo),
                        contentDescription = "MznLive Vendors Emblem Logo",
                        modifier = Modifier.size(142.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Branding Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MznLive",
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vendors",
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = MznRedAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                LivePillBadge()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CONNECT • SELL • GROW TOGETHER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MznGold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Official Companion App for MznLive",
                fontSize = 12.sp,
                color = MznBlueLight
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MznBlueLight,
                strokeWidth = 2.5.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = MznBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("splash_continue_btn")
            ) {
                Text("Enter Vendor Portal", fontWeight = FontWeight.Bold)
            }
        }
    }
}
