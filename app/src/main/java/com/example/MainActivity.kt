package com.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.JariditViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.JariditTopBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.ArcCyan
import com.example.ui.theme.ArcCyanDark
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DarkVoid
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: JariditViewModel = viewModel()
                val profile by viewModel.profile.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val memories by viewModel.memories.collectAsStateWithLifecycle()
                val projects by viewModel.projects.collectAsStateWithLifecycle()
                val studyTasks by viewModel.studyTasks.collectAsStateWithLifecycle()
                val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
                val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val pendingMemoryPrompt by viewModel.pendingMemoryPrompt.collectAsStateWithLifecycle()

                // Speech recognition launchers
                val speechRecognizerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val spokenText = result.data
                            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            ?.firstOrNull()
                        if (!spokenText.isNullOrBlank()) {
                            viewModel.sendMessage(spokenText, isVoice = true)
                        }
                    }
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARIDIT...")
                        }
                        try {
                            speechRecognizerLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@MainActivity,
                                "Speech recognition unavailable: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Microphone permission needed for voice input",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                fun startVoiceInput() {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                fun shareUniversalLink() {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Check out JARIDIT, my personal JARVIS-style assistant: ${JariditViewModel.UNIVERSAL_WEB_LINK}"
                        )
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(sendIntent, "Share JARIDIT Universal Link"))
                }

                if (!profile.onboardingCompleted) {
                    OnboardingScreen(
                        onComplete = { callName, interests, subjects, biggestProject, humor ->
                            viewModel.completeOnboarding(
                                callName,
                                interests,
                                subjects,
                                biggestProject,
                                humor
                            )
                        },
                        onSpeakGreeting = { greeting ->
                            viewModel.speak(greeting)
                        }
                    )
                } else {
                    BackHandler(enabled = currentTab != ScreenTab.HOME) {
                        viewModel.setTab(ScreenTab.HOME)
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkVoid),
                        topBar = {
                            JariditTopBar(
                                isSpeaking = isSpeaking,
                                isThinking = isAiThinking,
                                isLiked = profile.isLiked,
                                likesCount = profile.likesCount,
                                onToggleLike = { viewModel.toggleLike() },
                                onShareUniversalLink = { shareUniversalLink() },
                                onStopSpeaking = { viewModel.stopSpeaking() }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = DarkCharcoal,
                                modifier = Modifier.testTag("main_bottom_nav")
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.HOME,
                                    onClick = { viewModel.setTab(ScreenTab.HOME) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = "Chat",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Chat",
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DarkVoid,
                                        selectedTextColor = ArcCyan,
                                        indicatorColor = ArcCyan,
                                        unselectedIconColor = TextTertiary,
                                        unselectedTextColor = TextTertiary
                                    ),
                                    modifier = Modifier.testTag("nav_tab_home")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.MEMORY,
                                    onClick = { viewModel.setTab(ScreenTab.MEMORY) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Memory,
                                            contentDescription = "Memory",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Memory",
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == ScreenTab.MEMORY) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DarkVoid,
                                        selectedTextColor = ArcCyan,
                                        indicatorColor = ArcCyan,
                                        unselectedIconColor = TextTertiary,
                                        unselectedTextColor = TextTertiary
                                    ),
                                    modifier = Modifier.testTag("nav_tab_memory")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.PROJECTS,
                                    onClick = { viewModel.setTab(ScreenTab.PROJECTS) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = "Projects",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Projects",
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == ScreenTab.PROJECTS) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DarkVoid,
                                        selectedTextColor = ArcCyan,
                                        indicatorColor = ArcCyan,
                                        unselectedIconColor = TextTertiary,
                                        unselectedTextColor = TextTertiary
                                    ),
                                    modifier = Modifier.testTag("nav_tab_projects")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.STUDY,
                                    onClick = { viewModel.setTab(ScreenTab.STUDY) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = "Study",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Study",
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == ScreenTab.STUDY) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DarkVoid,
                                        selectedTextColor = ArcCyan,
                                        indicatorColor = ArcCyan,
                                        unselectedIconColor = TextTertiary,
                                        unselectedTextColor = TextTertiary
                                    ),
                                    modifier = Modifier.testTag("nav_tab_study")
                                )

                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.SETTINGS,
                                    onClick = { viewModel.setTab(ScreenTab.SETTINGS) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            "Config",
                                            fontSize = 10.sp,
                                            fontWeight = if (currentTab == ScreenTab.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DarkVoid,
                                        selectedTextColor = ArcCyan,
                                        indicatorColor = ArcCyan,
                                        unselectedIconColor = TextTertiary,
                                        unselectedTextColor = TextTertiary
                                    ),
                                    modifier = Modifier.testTag("nav_tab_settings")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                ScreenTab.HOME -> {
                                    HomeScreen(
                                        messages = chatMessages,
                                        profile = profile,
                                        projects = projects,
                                        isAiThinking = isAiThinking,
                                        isSpeaking = isSpeaking,
                                        pendingMemoryPrompt = pendingMemoryPrompt,
                                        onSendMessage = { text, isVoice ->
                                            viewModel.sendMessage(text, isVoice)
                                        },
                                        onSpeakMessage = { text ->
                                            viewModel.speak(text)
                                        },
                                        onApproveMemory = { prompt ->
                                            viewModel.approveMemory(prompt)
                                        },
                                        onDismissMemoryPrompt = {
                                            viewModel.dismissMemoryPrompt()
                                        },
                                        onLaunchVoiceInput = {
                                            startVoiceInput()
                                        },
                                        onToggleLike = {
                                            viewModel.toggleLike()
                                        },
                                        onShareUniversalLink = {
                                            shareUniversalLink()
                                        }
                                    )
                                }
                                ScreenTab.MEMORY -> {
                                    MemoryScreen(
                                        memories = memories,
                                        onAddMemory = { title, detail, cat, isSens ->
                                            viewModel.addMemory(title, detail, cat, isSens)
                                        },
                                        onUpdateMemory = { item ->
                                            viewModel.updateMemory(item)
                                        },
                                        onDeleteMemory = { item ->
                                            viewModel.deleteMemory(item)
                                        },
                                        onClearAllMemories = {
                                            viewModel.clearAllMemories()
                                        }
                                    )
                                }
                                ScreenTab.PROJECTS -> {
                                    ProjectsScreen(
                                        viewModel = viewModel,
                                        projects = projects,
                                        onConsultProject = { proj ->
                                            viewModel.setTab(ScreenTab.HOME)
                                            viewModel.sendMessage(
                                                "JARIDIT, let's analyze project status for '${proj.title}'. Description: ${proj.description}. Current progress: ${proj.progress}%. What should our next priority milestone be?"
                                            )
                                        }
                                    )
                                }
                                ScreenTab.STUDY -> {
                                    StudyScreen(
                                        tasks = studyTasks,
                                        schoolSubjects = profile.schoolSubjects,
                                        onAddTask = { subject, title, due, pri, notes ->
                                            viewModel.addStudyTask(subject, title, due, pri, notes)
                                        },
                                        onToggleTask = { task ->
                                            viewModel.toggleStudyTask(task)
                                        },
                                        onDeleteTask = { task ->
                                            viewModel.deleteStudyTask(task)
                                        },
                                        onLaunchStudyPrompt = { prompt ->
                                            viewModel.setTab(ScreenTab.HOME)
                                            viewModel.sendMessage(prompt)
                                        }
                                    )
                                }
                                ScreenTab.SETTINGS -> {
                                    SettingsScreen(
                                        profile = profile,
                                        onUpdateProfile = { newProf ->
                                            viewModel.updateProfile(newProf)
                                        },
                                        onTestVoice = {
                                            viewModel.speak("All systems nominal, ${profile.callName}. Neural voice synthesis engine calibrated.")
                                        },
                                        onClearChat = {
                                            viewModel.clearChatHistory()
                                        },
                                        onClearMemories = {
                                            viewModel.clearAllMemories()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
