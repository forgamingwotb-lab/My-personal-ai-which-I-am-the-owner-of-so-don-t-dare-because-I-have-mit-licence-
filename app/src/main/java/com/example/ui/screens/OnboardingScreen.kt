package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArcReactorCoreView
import com.example.ui.components.HudCard
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
fun OnboardingScreen(
    onComplete: (callName: String, interests: String, subjects: String, biggestProject: String, humor: String) -> Unit,
    onSpeakGreeting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var callName by remember { mutableStateOf("Parker") }
    var interests by remember { mutableStateOf("Technology, AI, Gaming, Science, Robotics, Energy Systems") }
    var schoolSubjects by remember { mutableStateOf("Physics, Math, Computer Science") }
    var biggestProject by remember { mutableStateOf("Practical Exosuit & Arc Reactor Research") }
    var humorStyle by remember { mutableStateOf("Balanced Desk Humor") }

    val welcomeSpeech = "Welcome back, Parker. I’m JARIDIT—your personal AI assistant. I can help with school, projects, research, planning, and the occasional highly questionable science idea. What are we working on today?"

    LaunchedEffect(Unit) {
        // Provide greeting
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkVoid)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // AI Core Emblem
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                ArcReactorCoreView(size = 64.dp, isPulsing = true)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "JARIDIT // FIRST-BOOT",
                color = ArcCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Iconic Greeting Card
            HudCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ArcCyan.copy(alpha = 0.5f)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = ArcCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INITIAL TRANSMISSION",
                                color = ArcCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = { onSpeakGreeting(welcomeSpeech) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("speak_greeting_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play Speech",
                                tint = ArcCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“$welcomeSpeech”",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "NEURAL PROFILE SETUP (5 QUESTIONS)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Question 1: Call Name
            SetupQuestionCard(
                questionNum = "1",
                question = "What should I call you?"
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    listOf("Parker", "Bro", "Dude", "Sir").forEach { tag ->
                        val isSelected = callName == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) ArcCyan else SurfaceCardElevated)
                                .clickable { callName = tag }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
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
                OutlinedTextField(
                    value = callName,
                    onValueChange = { callName = it },
                    label = { Text("Call Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_call_name")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question 2: Interests
            SetupQuestionCard(
                questionNum = "2",
                question = "What are your main interests?"
            ) {
                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Interests (e.g. Science, Gaming, Tech, AI)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_interests")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question 3: School Subjects
            SetupQuestionCard(
                questionNum = "3",
                question = "What school subjects do you want help with?"
            ) {
                OutlinedTextField(
                    value = schoolSubjects,
                    onValueChange = { schoolSubjects = it },
                    label = { Text("School Subjects (e.g. Physics, Math, CS)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_subjects")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question 4: Biggest Project
            SetupQuestionCard(
                questionNum = "4",
                question = "What is your biggest current project?"
            ) {
                OutlinedTextField(
                    value = biggestProject,
                    onValueChange = { biggestProject = it },
                    label = { Text("Flagship Project / Ambition") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_project")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question 5: Humor Style
            SetupQuestionCard(
                questionNum = "5",
                question = "What kind of humor & response style do you prefer?"
            ) {
                listOf(
                    "Balanced Desk Humor" to "Clever, dry humor, Marvel references, friendly tone",
                    "Dry & Witty" to "Sharp desk humor, sarcastic but supportive",
                    "Direct & Technical" to "Focused, scientific, concise with minimal jokes"
                ).forEach { (style, desc) ->
                    val isSelected = humorStyle == style
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ArcCyan.copy(alpha = 0.15f) else SurfaceCardElevated)
                            .border(1.dp, if (isSelected) ArcCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { humorStyle = style }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = style,
                                color = if (isSelected) ArcCyan else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Initialize Button
            Button(
                onClick = {
                    onComplete(callName, interests, schoolSubjects, biggestProject, humorStyle)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArcCyan,
                    contentColor = DarkVoid
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("initialize_jaridit_btn")
            ) {
                Text(
                    text = "INITIALIZE NEURAL CORE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SetupQuestionCard(
    questionNum: String,
    question: String,
    content: @Composable () -> Unit
) {
    HudCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ArcCyanDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = questionNum,
                        color = ArcCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
