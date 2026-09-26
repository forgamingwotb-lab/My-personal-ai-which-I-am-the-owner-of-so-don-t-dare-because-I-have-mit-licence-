package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PendingMemoryPrompt
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ArcCyanDark
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.PlasmaBlue
import com.example.ui.theme.ReactorMint
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArcReactorCoreView(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val outerRadius = this.size.minDimension / 2 - 2.dp.toPx()
        val innerRadius = outerRadius * 0.6f
        val coreRadius = outerRadius * 0.32f

        // Outer glow
        drawCircle(
            color = ArcCyan.copy(alpha = if (isPulsing) pulseAlpha * 0.3f else 0.2f),
            radius = outerRadius + 2.dp.toPx()
        )

        // Outer ring
        drawCircle(
            color = ArcCyanDark,
            radius = outerRadius,
            style = Stroke(width = 2.dp.toPx())
        )

        // Segments on ring
        val segments = 8
        for (i in 0 until segments) {
            val angle = Math.toRadians((i * (360.0 / segments) + rotationAngle).toDouble())
            val startX = center.x + (innerRadius * Math.cos(angle)).toFloat()
            val startY = center.y + (innerRadius * Math.sin(angle)).toFloat()
            val endX = center.x + (outerRadius * Math.cos(angle)).toFloat()
            val endY = center.y + (outerRadius * Math.sin(angle)).toFloat()
            drawLine(
                color = ArcCyan.copy(alpha = if (isPulsing) pulseAlpha else 0.7f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Inner circle
        drawCircle(
            color = PlasmaBlue.copy(alpha = 0.8f),
            radius = innerRadius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Core glowing energy point
        drawCircle(
            color = Color.White.copy(alpha = if (isPulsing) pulseAlpha else 0.9f),
            radius = coreRadius
        )
        drawCircle(
            color = ArcCyan,
            radius = coreRadius * 0.7f
        )
    }
}

@Composable
fun JariditTopBar(
    isSpeaking: Boolean,
    isThinking: Boolean,
    isLiked: Boolean = false,
    likesCount: Int = 42,
    onToggleLike: () -> Unit = {},
    onShareUniversalLink: () -> Unit = {},
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_bar_hud"),
        color = DarkVoid
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ArcReactorCoreView(
                        size = 36.dp,
                        isPulsing = isThinking || isSpeaking
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "JARIDIT",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isThinking) ArcCyan else ReactorMint)
                            )
                        }
                        Text(
                            text = when {
                                isThinking -> "NEURAL COMPUTING..."
                                isSpeaking -> "AUDIO TRANSMITTING"
                                else -> "SYSTEMS NOMINAL • v2.4"
                            },
                            color = if (isThinking || isSpeaking) ArcCyan else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isSpeaking) {
                        Button(
                            onClick = onStopSpeaking,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CriticalRed.copy(alpha = 0.2f),
                                contentColor = CriticalRed
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .border(1.dp, CriticalRed.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .testTag("stop_speaking_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Speaking",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("STOP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Like Button
                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isLiked) Color(0xFFFF4081).copy(alpha = 0.2f) else SurfaceCard)
                            .testTag("like_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like JARIDIT",
                                tint = if (isLiked) Color(0xFFFF4081) else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Universal Share Link Button
                    IconButton(
                        onClick = onShareUniversalLink,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ArcCyanDark.copy(alpha = 0.35f))
                            .border(1.dp, ArcCyan.copy(alpha = 0.4f), CircleShape)
                            .testTag("universal_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Universal Link",
                            tint = ArcCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // High-tech separator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                ArcCyan.copy(alpha = 0.6f),
                                PlasmaBlue.copy(alpha = 0.3f),
                                SurfaceBorder
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    borderColor: Color = SurfaceBorder,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
fun MemoryApprovalBanner(
    prompt: PendingMemoryPrompt,
    onApprove: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.5.dp, ArcCyan.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("memory_approval_banner")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = ArcCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEW MEMORY DETECTED",
                        color = ArcCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = prompt.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = prompt.detail,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Skip", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("approve_memory_btn")
                ) {
                    Text("Save to Memory", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
