package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val callName: String = "Parker",
    val humorStyle: String = "Balanced Desk Humor",
    val interests: String = "Technology, AI, Gaming, Science, Robotics, Energy Systems",
    val schoolSubjects: String = "Physics, Math, Computer Science",
    val currentAmbition: String = "Practical Exosuit & Grounded Arc Reactor Research",
    val ttsEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val aiModel: String = "gemini-3.5-flash",
    val autoMemoryPrompt: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val isLiked: Boolean = false,
    val likesCount: Int = 42
)

class UserSettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jaridit_user_settings", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            callName = prefs.getString("callName", "Parker") ?: "Parker",
            humorStyle = prefs.getString("humorStyle", "Balanced Desk Humor") ?: "Balanced Desk Humor",
            interests = prefs.getString(
                "interests",
                "Technology, AI, Gaming, Science, Robotics, Energy Systems"
            ) ?: "Technology, AI, Gaming, Science, Robotics, Energy Systems",
            schoolSubjects = prefs.getString(
                "schoolSubjects",
                "Physics, Math, Computer Science"
            ) ?: "Physics, Math, Computer Science",
            currentAmbition = prefs.getString(
                "currentAmbition",
                "Practical Exosuit & Grounded Arc Reactor Research"
            ) ?: "Practical Exosuit & Grounded Arc Reactor Research",
            ttsEnabled = prefs.getBoolean("ttsEnabled", true),
            autoSpeak = prefs.getBoolean("autoSpeak", false),
            voicePitch = prefs.getFloat("voicePitch", 1.0f),
            voiceSpeed = prefs.getFloat("voiceSpeed", 1.0f),
            aiModel = prefs.getString("aiModel", "gemini-3.5-flash") ?: "gemini-3.5-flash",
            autoMemoryPrompt = prefs.getBoolean("autoMemoryPrompt", true),
            onboardingCompleted = prefs.getBoolean("onboardingCompleted", false),
            isLiked = prefs.getBoolean("isLiked", false),
            likesCount = prefs.getInt("likesCount", 42)
        )
    }

    fun updateProfile(newProfile: UserProfile) {
        prefs.edit()
            .putString("callName", newProfile.callName)
            .putString("humorStyle", newProfile.humorStyle)
            .putString("interests", newProfile.interests)
            .putString("schoolSubjects", newProfile.schoolSubjects)
            .putString("currentAmbition", newProfile.currentAmbition)
            .putBoolean("ttsEnabled", newProfile.ttsEnabled)
            .putBoolean("autoSpeak", newProfile.autoSpeak)
            .putFloat("voicePitch", newProfile.voicePitch)
            .putFloat("voiceSpeed", newProfile.voiceSpeed)
            .putString("aiModel", newProfile.aiModel)
            .putBoolean("autoMemoryPrompt", newProfile.autoMemoryPrompt)
            .putBoolean("onboardingCompleted", newProfile.onboardingCompleted)
            .putBoolean("isLiked", newProfile.isLiked)
            .putInt("likesCount", newProfile.likesCount)
            .apply()
        _profile.value = newProfile
    }

    fun setOnboardingComplete(completed: Boolean) {
        prefs.edit().putBoolean("onboardingCompleted", completed).apply()
        _profile.value = _profile.value.copy(onboardingCompleted = completed)
    }
}
