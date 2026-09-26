package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.UserProfile
import com.example.ui.components.HudCard
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ArcCyanDark
import com.example.ui.theme.CriticalRed
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
fun SettingsScreen(
    profile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onTestVoice: () -> Unit,
    onClearChat: () -> Unit,
    onClearMemories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var callName by remember(profile.callName) { mutableStateOf(profile.callName) }
    var humorStyle by remember(profile.humorStyle) { mutableStateOf(profile.humorStyle) }
    var ttsEnabled by remember(profile.ttsEnabled) { mutableStateOf(profile.ttsEnabled) }
    var autoSpeak by remember(profile.autoSpeak) { mutableStateOf(profile.autoSpeak) }
    var voiceSpeed by remember(profile.voiceSpeed) { mutableFloatStateOf(profile.voiceSpeed) }
    var voicePitch by remember(profile.voicePitch) { mutableFloatStateOf(profile.voicePitch) }
    var autoMemoryPrompt by remember(profile.autoMemoryPrompt) { mutableStateOf(profile.autoMemoryPrompt) }
    var aiModel by remember(profile.aiModel) { mutableStateOf(profile.aiModel) }

    var showClearChatDialog by remember { mutableStateOf(false) }
    var showClearMemoriesDialog by remember { mutableStateOf(false) }

    val hasApiKey = try {
        BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"
    } catch (e: Throwable) {
        false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Header
            item {
                HudCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ArcCyan.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ArcCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COMMAND CENTER // SETTINGS & CORE CONFIG",
                            color = ArcCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Personality Settings
            item {
                SectionHeader(title = "PERSONALITY & CALL SIGN")
                HudCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "How JARIDIT addresses you:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Parker", "Bro", "Dude", "Sir").forEach { tag ->
                                val isSelected = callName == tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) ArcCyan else SurfaceCardElevated)
                                        .clickable {
                                            callName = tag
                                            onUpdateProfile(profile.copy(callName = tag))
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("nickname_tag_$tag")
                                ) {
                                    Text(
                                        text = tag,
                                        color = if (isSelected) DarkVoid else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = callName,
                            onValueChange = {
                                callName = it
                                onUpdateProfile(profile.copy(callName = it))
                            },
                            label = { Text("Custom Call Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ArcCyan,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Humor & Response Tone:",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf("Balanced Desk Humor", "Dry & Witty", "Direct & Technical").forEach { style ->
                            val isSelected = humorStyle == style
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        humorStyle = style
                                        onUpdateProfile(profile.copy(humorStyle = style))
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) ArcCyan else SurfaceCardElevated)
                                        .border(1.dp, if (isSelected) ArcCyan else SurfaceBorder, RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = style,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Voice & Speech Settings
            item {
                SectionHeader(title = "VOICE ENGINE & SYNTHESIS")
                HudCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Speech-to-Text & Voice Output", color = TextPrimary, fontSize = 13.sp)
                                Text("Enable speech synthesis for responses", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = ttsEnabled,
                                onCheckedChange = {
                                    ttsEnabled = it
                                    onUpdateProfile(profile.copy(ttsEnabled = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = ArcCyan, checkedTrackColor = ArcCyanDark)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Auto-Speak Responses", color = TextPrimary, fontSize = 13.sp)
                                Text("Automatically read every incoming message", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = autoSpeak,
                                onCheckedChange = {
                                    autoSpeak = it
                                    onUpdateProfile(profile.copy(autoSpeak = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = ArcCyan, checkedTrackColor = ArcCyanDark)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Speech Rate (${String.format("%.1f", voiceSpeed)}x):", color = TextSecondary, fontSize = 11.sp)
                        Slider(
                            value = voiceSpeed,
                            onValueChange = {
                                voiceSpeed = it
                                onUpdateProfile(profile.copy(voiceSpeed = it))
                            },
                            valueRange = 0.5f..1.8f,
                            colors = SliderDefaults.colors(thumbColor = ArcCyan, activeTrackColor = ArcCyan),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Voice Pitch (${String.format("%.1f", voicePitch)}):", color = TextSecondary, fontSize = 11.sp)
                        Slider(
                            value = voicePitch,
                            onValueChange = {
                                voicePitch = it
                                onUpdateProfile(profile.copy(voicePitch = it))
                            },
                            valueRange = 0.5f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = ArcCyan, activeTrackColor = ArcCyan),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = onTestVoice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_voice_btn")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ArcCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Voice Synthesis", color = ArcCyan)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Model & Neural Uplink
            item {
                SectionHeader(title = "AI BRAIN & MODEL RUNTIME")
                HudCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = if (hasApiKey) ReactorMint else ArcCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gemini API Uplink", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (hasApiKey) ReactorMint.copy(alpha = 0.2f) else ArcCyanDark)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (hasApiKey) "ACTIVE" else "READY / BUILTIN",
                                    color = if (hasApiKey) ReactorMint else ArcCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Managed securely via AI Studio Secrets (`GEMINI_API_KEY`). Direct REST API reasoning engine with context awareness and memory integration.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Active Gemini Model:", color = TextSecondary, fontSize = 11.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("gemini-3.5-flash", "gemini-3.1-pro-preview").forEach { m ->
                                val isSelected = aiModel == m
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ArcCyan else SurfaceCardElevated)
                                        .clickable {
                                            aiModel = m
                                            onUpdateProfile(profile.copy(aiModel = m))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = m,
                                        color = if (isSelected) DarkVoid else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Memory & Privacy
            item {
                SectionHeader(title = "MEMORY VAULT & PRIVACY")
                HudCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Memory Extraction Prompts", color = TextPrimary, fontSize = 13.sp)
                                Text("Ask for confirmation before saving insights", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = autoMemoryPrompt,
                                onCheckedChange = {
                                    autoMemoryPrompt = it
                                    onUpdateProfile(profile.copy(autoMemoryPrompt = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = ArcCyan, checkedTrackColor = ArcCyanDark)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showClearChatDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clear_chat_history_btn")
                            ) {
                                Text("Clear Chat", color = CriticalRed, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showClearMemoriesDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clear_memories_settings_btn")
                            ) {
                                Text("Purge Vault", color = CriticalRed, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Clear Chat Dialog
        if (showClearChatDialog) {
            AlertDialog(
                onDismissRequest = { showClearChatDialog = false },
                title = { Text("Clear Conversation History?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("This will erase all messages in the active chat. Projects and memories will remain preserved.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            onClearChat()
                            showClearChatDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed)
                    ) {
                        Text("Clear History", color = DarkVoid, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearChatDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkCharcoal
            )
        }

        // Clear Memories Dialog
        if (showClearMemoriesDialog) {
            AlertDialog(
                onDismissRequest = { showClearMemoriesDialog = false },
                title = { Text("Purge All Saved Memories?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("This will delete all stored preferences, goals, and facts from JARIDIT's memory vault.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            onClearMemories()
                            showClearMemoriesDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed)
                    ) {
                        Text("Purge Vault", color = DarkVoid, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearMemoriesDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkCharcoal
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = ArcCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
