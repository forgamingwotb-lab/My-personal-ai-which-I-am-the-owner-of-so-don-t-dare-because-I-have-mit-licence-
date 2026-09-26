package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ai.GeminiClient
import com.example.data.ChatMessage
import com.example.data.JariditDatabase
import com.example.data.MemoryItem
import com.example.data.ProjectItem
import com.example.data.ProjectMilestone
import com.example.data.StudyTask
import com.example.data.UserProfile
import com.example.data.UserSettingsRepository
import com.example.voice.TextToSpeechEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    MEMORY,
    PROJECTS,
    STUDY,
    SETTINGS
}

data class PendingMemoryPrompt(
    val title: String,
    val detail: String,
    val category: String = "FACT"
)

class JariditViewModel(application: Application) : AndroidViewModel(application) {

    private val db: JariditDatabase = Room.databaseBuilder(
        application,
        JariditDatabase::class.java,
        "jaridit_database.db"
    ).fallbackToDestructiveMigration().build()

    private val userSettingsRepo = UserSettingsRepository(application)
    private val geminiClient = GeminiClient()
    val ttsEngine = TextToSpeechEngine(application)

    val profile: StateFlow<UserProfile> = userSettingsRepo.profile

    val chatMessages: StateFlow<List<ChatMessage>> = db.chatDao().getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryItem>> = db.memoryDao().getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectItem>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> = db.studyDao().getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSpeaking: StateFlow<Boolean> = ttsEngine.isSpeaking

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _pendingMemoryPrompt = MutableStateFlow<PendingMemoryPrompt?>(null)
    val pendingMemoryPrompt: StateFlow<PendingMemoryPrompt?> = _pendingMemoryPrompt.asStateFlow()

    companion object {
        const val UNIVERSAL_WEB_LINK = "https://ais-dev-hwz5swemnd2kvudd6w3jh2-72963549889.asia-southeast1.run.app"
    }

    fun toggleLike() {
        val current = profile.value
        val newIsLiked = !current.isLiked
        val newCount = if (newIsLiked) current.likesCount + 1 else (current.likesCount - 1).coerceAtLeast(0)
        updateProfile(current.copy(isLiked = newIsLiked, likesCount = newCount))
    }

    init {
        viewModelScope.launch {
            seedDefaultDataIfNeeded()
        }
    }

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    private suspend fun seedDefaultDataIfNeeded() {
        if (db.projectDao().count() == 0) {
            val projectId = db.projectDao().insertProject(
                ProjectItem(
                    title = "Arc Reactor Research",
                    description = "Grounded research into magnetic plasma confinement, solid-state electronics, toroidal coils, and high-density graphene supercapacitors. No radioactive materials.",
                    status = "ACTIVE",
                    progress = 35,
                    category = "ENGINEERING",
                    notes = "Core objective: Low-temperature plasma confinement geometry & thermoelectric energy harvesting."
                )
            )
            db.projectDao().insertMilestone(
                ProjectMilestone(projectId = projectId, title = "Analyze toroidal magnetic field constraints", isCompleted = true)
            )
            db.projectDao().insertMilestone(
                ProjectMilestone(projectId = projectId, title = "Model solid-state supercapacitor discharge rates", isCompleted = true)
            )
            db.projectDao().insertMilestone(
                ProjectMilestone(projectId = projectId, title = "Design brushless servo actuator harness for arm joints", isCompleted = false)
            )
            db.projectDao().insertMilestone(
                ProjectMilestone(projectId = projectId, title = "Stress-test titanium-aluminide composite specs", isCompleted = false)
            )
        }

        if (db.memoryDao().count() == 0) {
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Primary Ambition",
                    detail = "Aims to build practical Iron Man-style exosuits and clean energy systems grounded in real physics.",
                    category = "GOAL"
                )
            )
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Core Interests",
                    detail = "Technology, AI, science, gaming, electronics, energy systems, and robotics.",
                    category = "INTEREST"
                )
            )
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Arc Reactor Safety Rule",
                    detail = "Must strictly avoid radioactive materials or hazardous experiments; focus on magnetic fields, plasma, and supercapacitors.",
                    category = "PREFERENCE"
                )
            )
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Student Context",
                    detail = "Currently in school; appreciates step-by-step, plain-English explanations rather than dense textbook jargon.",
                    category = "SCHOOL"
                )
            )
        }

        if (db.studyDao().count() == 0) {
            db.studyDao().insertTask(
                StudyTask(
                    subject = "Physics",
                    taskTitle = "Electromagnetism & Magnetic Flux Problem Set",
                    dueDate = "Tomorrow, 5:00 PM",
                    priority = "HIGH",
                    isCompleted = false,
                    notes = "Focus on Faraday's Law and toroidal coil field calculations."
                )
            )
            db.studyDao().insertTask(
                StudyTask(
                    subject = "Mathematics",
                    taskTitle = "Calculus II: Integration Techniques Review",
                    dueDate = "Friday",
                    priority = "MEDIUM",
                    isCompleted = false,
                    notes = "Partial fraction decomposition and trig substitutions."
                )
            )
        }

        val recentChat = db.chatDao().getRecentMessages(1)
        if (recentChat.isEmpty()) {
            val callName = profile.value.callName
            db.chatDao().insertMessage(
                ChatMessage(
                    sender = "JARIDIT",
                    message = "Welcome back, $callName. I’m JARIDIT—your personal AI assistant. I can help with school, projects, research, planning, and the occasional highly questionable science idea. What are we working on today?"
                )
            )
        }
    }

    fun sendMessage(text: String, isVoice: Boolean = false) {
        if (text.isBlank()) return
        val userMsg = text.trim()

        viewModelScope.launch {
            db.chatDao().insertMessage(
                ChatMessage(
                    sender = "USER",
                    message = userMsg,
                    isVoice = isVoice
                )
            )

            _isAiThinking.value = true

            val currentProfile = profile.value
            val history = db.chatDao().getRecentMessages(12).reversed()
            val currentMemories = memories.value
            val currentProjects = projects.value

            val (aiReply, suggestedMemory) = geminiClient.generateResponse(
                userInput = userMsg,
                conversationHistory = history,
                userProfile = currentProfile,
                memories = currentMemories,
                projects = currentProjects
            )

            db.chatDao().insertMessage(
                ChatMessage(
                    sender = "JARIDIT",
                    message = aiReply,
                    suggestedMemory = suggestedMemory
                )
            )

            _isAiThinking.value = false

            if (currentProfile.autoSpeak && currentProfile.ttsEnabled) {
                ttsEngine.speak(aiReply, currentProfile.voicePitch, currentProfile.voiceSpeed)
            }

            if (!suggestedMemory.isNullOrBlank() && currentProfile.autoMemoryPrompt) {
                val parts = suggestedMemory.split("|")
                val title = parts.getOrNull(0)?.trim() ?: "Conversation Insight"
                val detail = parts.getOrNull(1)?.trim() ?: suggestedMemory
                _pendingMemoryPrompt.value = PendingMemoryPrompt(title = title, detail = detail)
            }
        }
    }

    fun speak(text: String) {
        val currentProfile = profile.value
        ttsEngine.speak(text, currentProfile.voicePitch, currentProfile.voiceSpeed)
    }

    fun stopSpeaking() {
        ttsEngine.stop()
    }

    fun approveMemory(prompt: PendingMemoryPrompt) {
        viewModelScope.launch {
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = prompt.title,
                    detail = prompt.detail,
                    category = prompt.category
                )
            )
            _pendingMemoryPrompt.value = null
        }
    }

    fun dismissMemoryPrompt() {
        _pendingMemoryPrompt.value = null
    }

    // Memories CRUD
    fun addMemory(title: String, detail: String, category: String, isSensitive: Boolean = false) {
        viewModelScope.launch {
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = title.trim(),
                    detail = detail.trim(),
                    category = category,
                    isSensitive = isSensitive
                )
            )
        }
    }

    fun updateMemory(item: MemoryItem) {
        viewModelScope.launch {
            db.memoryDao().updateMemory(item)
        }
    }

    fun deleteMemory(item: MemoryItem) {
        viewModelScope.launch {
            db.memoryDao().deleteMemory(item)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            db.memoryDao().clearAllMemories()
        }
    }

    // Projects CRUD
    fun addProject(title: String, description: String, category: String) {
        viewModelScope.launch {
            db.projectDao().insertProject(
                ProjectItem(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    status = "ACTIVE",
                    progress = 10
                )
            )
        }
    }

    fun updateProject(item: ProjectItem) {
        viewModelScope.launch {
            db.projectDao().updateProject(item.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteProject(item: ProjectItem) {
        viewModelScope.launch {
            db.projectDao().deleteProject(item)
        }
    }

    fun getMilestonesForProject(projectId: Long) = db.projectDao().getMilestonesForProject(projectId)

    fun toggleMilestone(milestone: ProjectMilestone, allMilestones: List<ProjectMilestone>, project: ProjectItem) {
        viewModelScope.launch {
            val updated = milestone.copy(isCompleted = !milestone.isCompleted)
            db.projectDao().updateMilestone(updated)

            // Recalculate progress percentage
            val updatedList = allMilestones.map { if (it.id == milestone.id) updated else it }
            val completedCount = updatedList.count { it.isCompleted }
            val newProgress = if (updatedList.isNotEmpty()) {
                (completedCount * 100) / updatedList.size
            } else project.progress

            db.projectDao().updateProject(project.copy(progress = newProgress, updatedAt = System.currentTimeMillis()))
        }
    }

    fun addMilestone(projectId: Long, title: String) {
        viewModelScope.launch {
            db.projectDao().insertMilestone(
                ProjectMilestone(projectId = projectId, title = title.trim(), isCompleted = false)
            )
        }
    }

    fun deleteMilestone(milestone: ProjectMilestone) {
        viewModelScope.launch {
            db.projectDao().deleteMilestone(milestone)
        }
    }

    // Study Tasks CRUD
    fun addStudyTask(subject: String, taskTitle: String, dueDate: String, priority: String, notes: String = "") {
        viewModelScope.launch {
            db.studyDao().insertTask(
                StudyTask(
                    subject = subject.trim(),
                    taskTitle = taskTitle.trim(),
                    dueDate = dueDate.trim(),
                    priority = priority,
                    notes = notes.trim()
                )
            )
        }
    }

    fun toggleStudyTask(task: StudyTask) {
        viewModelScope.launch {
            db.studyDao().updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteStudyTask(task: StudyTask) {
        viewModelScope.launch {
            db.studyDao().deleteTask(task)
        }
    }

    // Settings & Profile
    fun updateProfile(profile: UserProfile) {
        userSettingsRepo.updateProfile(profile)
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            db.chatDao().clearHistory()
            val callName = profile.value.callName
            db.chatDao().insertMessage(
                ChatMessage(
                    sender = "JARIDIT",
                    message = "Tactical logs cleared. Ready for your next directive, $callName."
                )
            )
        }
    }

    fun completeOnboarding(
        callName: String,
        interests: String,
        schoolSubjects: String,
        biggestProject: String,
        humor: String
    ) {
        val updated = profile.value.copy(
            callName = callName.ifBlank { "Parker" },
            interests = interests.ifBlank { "Technology, AI, Gaming, Science, Robotics" },
            schoolSubjects = schoolSubjects.ifBlank { "Physics, Math, Computer Science" },
            currentAmbition = biggestProject.ifBlank { "Practical Exosuit & Grounded Arc Reactor Research" },
            humorStyle = humor,
            onboardingCompleted = true
        )
        userSettingsRepo.updateProfile(updated)

        viewModelScope.launch {
            // Save configured details to memory
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Preferred Call Name",
                    detail = "Address the user as '$callName'.",
                    category = "PROFILE"
                )
            )
            db.memoryDao().insertMemory(
                MemoryItem(
                    title = "Flagship Project Goal",
                    detail = biggestProject,
                    category = "PROJECT"
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
    }
}
