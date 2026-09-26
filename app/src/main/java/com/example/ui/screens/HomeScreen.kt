package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.ProjectItem
import com.example.data.UserProfile
import com.example.ui.PendingMemoryPrompt
import com.example.ui.components.ArcReactorCoreView
import com.example.ui.components.MemoryApprovalBanner
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ArcCyanDark
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.PlasmaBlue
import com.example.ui.theme.ReactorMint
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    messages: List<ChatMessage>,
    profile: UserProfile,
    projects: List<ProjectItem>,
    isAiThinking: Boolean,
    isSpeaking: Boolean,
    pendingMemoryPrompt: PendingMemoryPrompt?,
    onSendMessage: (String, Boolean) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onApproveMemory: (PendingMemoryPrompt) -> Unit,
    onDismissMemoryPrompt: () -> Unit,
    onLaunchVoiceInput: () -> Unit,
    onToggleLike: () -> Unit = {},
    onShareUniversalLink: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
    ) {
        // Pending memory confirmation banner
        AnimatedVisibility(visible = pendingMemoryPrompt != null) {
            pendingMemoryPrompt?.let { prompt ->
                MemoryApprovalBanner(
                    prompt = prompt,
                    onApprove = { onApproveMemory(prompt) },
                    onDismiss = onDismissMemoryPrompt
                )
            }
        }

        // Main Chat Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // HUD Dashboard Snippet
            item {
                HudDashboardHeader(profile = profile, activeProjects = projects)
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Universal Access Link & Like Card
            item {
                UniversalLinkCard(
                    profile = profile,
                    onToggleLike = onToggleLike,
                    onShareLink = onShareUniversalLink,
                    onCopyLink = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText(
                                "JARIDIT Universal Link",
                                "https://ais-dev-hwz5swemnd2kvudd6w3jh2-72963549889.asia-southeast1.run.app"
                            )
                        )
                        Toast.makeText(context, "Universal link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Quick Actions Chips
            item {
                QuickActionChips(onChipClick = { prompt ->
                    onSendMessage(prompt, false)
                })
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Chat Bubbles
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeak = { onSpeakMessage(msg.message) },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("JARIDIT Chat", msg.message))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Thinking Indicator
            if (isAiThinking) {
                item {
                    AiThinkingCard()
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Voice Speaking / Listening Banner if active
        if (isSpeaking) {
            SpeakingWaveBanner()
        }

        // Input Bar
        BottomInputBar(
            inputText = inputText,
            isAiThinking = isAiThinking,
            onInputChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    onSendMessage(inputText, false)
                    inputText = ""
                }
            },
            onVoiceClick = onLaunchVoiceInput
        )
    }
}

@Composable
private fun HudDashboardHeader(
    profile: UserProfile,
    activeProjects: List<ProjectItem>
) {
    val topProject = activeProjects.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        SurfaceCard,
                        DarkCharcoal
                    )
                )
            )
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("hud_dashboard_header")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ArcReactorCoreView(size = 28.dp, isPulsing = true)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "COMMAND PROTOCOL // ${profile.callName.uppercase()}",
                            color = ArcCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "STYLE: ${profile.humorStyle.uppercase()}",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ArcCyanDark.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ONLINE",
                        color = ArcCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (topProject != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SurfaceBorder)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ACTIVE: ${topProject.title}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${topProject.progress}% COMPLETE",
                        color = PlasmaBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun UniversalLinkCard(
    profile: UserProfile,
    onToggleLike: () -> Unit,
    onShareLink: () -> Unit,
    onCopyLink: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, ArcCyan.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("universal_link_card")
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = ArcCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UNIVERSAL WEB LINK & ACCESS",
                        color = ArcCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Like Button with heart and count
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (profile.isLiked) Color(0xFFFF4081).copy(alpha = 0.2f) else SurfaceCardElevated)
                        .border(
                            1.dp,
                            if (profile.isLiked) Color(0xFFFF4081).copy(alpha = 0.6f) else SurfaceBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable(onClick = onToggleLike)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("universal_card_like_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (profile.isLiked) Color(0xFFFF4081) else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.likesCount}",
                            color = if (profile.isLiked) Color(0xFFFF4081) else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Universal link to open JARIDIT on your phone browser or computer:",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .clickable(onClick = onCopyLink)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "https://ais-dev-hwz5swemnd2kvudd6w3jh2-72963549889.asia-southeast1.run.app",
                    color = ArcCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyLink,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("copy_universal_link_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = ArcCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Link", fontSize = 11.sp, color = ArcCyan)
                }

                Button(
                    onClick = onShareLink,
                    colors = ButtonDefaults.buttonColors(containerColor = ArcCyan, contentColor = DarkVoid),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("share_universal_link_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun QuickActionChips(onChipClick: (String) -> Unit) {
    val prompts = listOf(
        "Arc Reactor Brainstorm" to "Let's review the Arc Reactor design: magnetic confinement, supercapacitors, and zero radiation. What's our next physical constraint?",
        "Homework Help" to "Bro, I have a tricky homework problem in school right now. Can we break it down step-by-step?",
        "Exosuit Materials" to "What are the most realistic, high-strength materials for an exoskeleton frame that won't weigh 200 kilos?",
        "Study Schedule" to "Can you help me design a balanced study plan for my school exams that still leaves time for my tech projects?",
        "Continue" to "Continue from our last point."
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { (label, prompt) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCardElevated)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
                    .clickable { onChipClick(prompt) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("quick_chip_$label")
            ) {
                Text(
                    text = label,
                    color = ArcCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    val isJaridit = message.sender == "JARIDIT"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (isJaridit) "chat_bubble_jaridit" else "chat_bubble_user"),
        horizontalAlignment = if (isJaridit) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, start = if (isJaridit) 4.dp else 0.dp, end = if (!isJaridit) 4.dp else 0.dp)
        ) {
            if (isJaridit) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "JARIDIT",
                    tint = ArcCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "JARIDIT",
                    color = ArcCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            } else {
                Text(
                    text = if (message.isVoice) "YOU (VOICE)" else "YOU",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isJaridit) 2.dp else 14.dp,
                        bottomEnd = if (isJaridit) 14.dp else 2.dp
                    )
                )
                .background(if (isJaridit) SurfaceCard else ArcCyanDark.copy(alpha = 0.45f))
                .border(
                    1.dp,
                    if (isJaridit) SurfaceBorder else ArcCyan.copy(alpha = 0.5f),
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isJaridit) 2.dp else 14.dp,
                        bottomEnd = if (isJaridit) 14.dp else 2.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = message.message,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                if (isJaridit) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("copy_chat_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("speak_chat_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read aloud",
                                tint = ArcCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiThinkingCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, ArcCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        CircularProgressIndicator(
            color = ArcCyan,
            strokeWidth = 2.dp,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "JARIDIT computing response...",
            color = ArcCyan.copy(alpha = alpha),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SpeakingWaveBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Wave"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcCyanDark.copy(alpha = 0.3f))
            .border(1.dp, ArcCyan.copy(alpha = 0.4f))
            .padding(vertical = 6.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = ArcCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SPEAKING AUDIO TRANSMISSION...",
                color = ArcCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun BottomInputBar(
    inputText: String,
    isAiThinking: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClick: () -> Unit
) {
    Surface(
        color = DarkCharcoal,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_input_bar")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SurfaceBorder)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice input button
                IconButton(
                    onClick = onVoiceClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceCardElevated)
                        .border(1.dp, ArcCyan.copy(alpha = 0.5f), CircleShape)
                        .testTag("mic_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = ArcCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = "Message JARIDIT...",
                            color = TextTertiary,
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ArcCyan
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp, max = 120.dp)
                        .testTag("chat_input_field"),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                IconButton(
                    onClick = onSend,
                    enabled = inputText.isNotBlank() && !isAiThinking,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) ArcCyan else SurfaceCardElevated)
                        .testTag("send_message_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) DarkVoid else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
