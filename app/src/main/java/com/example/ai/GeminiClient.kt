package com.example.ai

import com.example.BuildConfig
import com.example.data.ChatMessage
import com.example.data.MemoryItem
import com.example.data.ProjectItem
import com.example.data.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        userInput: String,
        conversationHistory: List<ChatMessage>,
        userProfile: UserProfile,
        memories: List<MemoryItem>,
        projects: List<ProjectItem>
    ): Pair<String, String?> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getIntelligentFallback(userInput, userProfile, memories, projects)
        }

        try {
            val systemPrompt = buildSystemPrompt(userProfile, memories, projects)
            val model = userProfile.aiModel.ifBlank { "gemini-3.5-flash" }
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System instruction
            val systemInstObj = JSONObject()
            val systemParts = JSONArray()
            systemParts.put(JSONObject().put("text", systemPrompt))
            systemInstObj.put("parts", systemParts)
            rootJson.put("systemInstruction", systemInstObj)

            // Generation config
            val configObj = JSONObject().apply {
                put("temperature", 0.75)
                put("topP", 0.95)
            }
            rootJson.put("generationConfig", configObj)

            // Contents array (Conversation history + current message)
            val contentsArray = JSONArray()

            // Include last 10 messages for context
            val recentMessages = conversationHistory.takeLast(10)
            for (msg in recentMessages) {
                val role = if (msg.sender == "USER") "user" else "model"
                val contentObj = JSONObject()
                contentObj.put("role", role)
                val parts = JSONArray()
                parts.put(JSONObject().put("text", msg.message))
                contentObj.put("parts", parts)
                contentsArray.put(contentObj)
            }

            // Current message
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", userInput))
            currentContent.put("parts", currentParts)
            contentsArray.put(currentContent)

            rootJson.put("contents", contentsArray)

            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // If API returned error (e.g. quota, bad key), gracefully fallback with notification
                return@withContext Pair(
                    "JARIDIT here. Network or API returned HTTP ${response.code}. Here's my direct tactical assessment:\n\n" +
                            getIntelligentFallback(userInput, userProfile, memories, projects).first,
                    null
                )
            }

            val respJson = JSONObject(responseString)
            val candidates = respJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val contentObj = firstCandidate?.optJSONObject("content")
            val partsArray = contentObj?.optJSONArray("parts")
            val text = partsArray?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                val parsed = extractMemorySuggestion(text)
                return@withContext parsed
            } else {
                return@withContext getIntelligentFallback(userInput, userProfile, memories, projects)
            }
        } catch (e: Exception) {
            return@withContext Pair(
                "My neural uplink ran into a slight glitch: ${e.localizedMessage ?: "Connection error"}. But on your left, here is my direct diagnostic:\n\n" +
                        getIntelligentFallback(userInput, userProfile, memories, projects).first,
                null
            )
        }
    }

    private fun extractMemorySuggestion(rawText: String): Pair<String, String?> {
        val memoryRegex = Regex("\\[SUGGESTED_MEMORY:(.*?)\\]")
        val match = memoryRegex.find(rawText)
        return if (match != null) {
            val memoryContent = match.groupValues[1].trim()
            val cleanText = rawText.replace(match.value, "").trim()
            Pair(cleanText, memoryContent)
        } else {
            Pair(rawText.trim(), null)
        }
    }

    private fun buildSystemPrompt(
        userProfile: UserProfile,
        memories: List<MemoryItem>,
        projects: List<ProjectItem>
    ): String {
        val callName = userProfile.callName.ifBlank { "Parker" }
        val memoriesText = if (memories.isEmpty()) {
            "- User ambition: Practical exosuit and grounded Arc Reactor research (no radiation).\n- Interests: Technology, science, AI, gaming."
        } else {
            memories.joinToString("\n") { "- [${it.category}] ${it.title}: ${it.detail}" }
        }

        val projectsText = if (projects.isEmpty()) {
            "- Arc Reactor Research: Researching magnetic plasma confinement, solid-state electronics, and high-density capacitors."
        } else {
            projects.joinToString("\n") { "- ${it.title} (${it.category}, ${it.progress}% complete): ${it.description}. Notes: ${it.notes}" }
        }

        return """
You are JARIDIT, a personal JARVIS-style assistant for $callName.
You speak casually and naturally, like a clever friend and futuristic assistant.

PERSONALITY RULES:
- Smart, helpful, witty, and supportive companion.
- Uses occasional dry or "desk-humor" jokes. Avoid childish humor.
- Can use Marvel, gaming, technology, and science references when they fit naturally.
- Uses phrases such as "I can do this all day" or "On your left" occasionally, but never overuses them.
- Calls the user "bro", "dude", "$callName", or "sir" when appropriate, without sounding repetitive.
- Encourages ambitions without making unrealistic promises. Never discourages simply because a goal is difficult.
- If something is unsafe or scientifically impossible, explain the reason clearly and suggest a safer or realistic path.
- Tone style preference: ${userProfile.humorStyle}.

USER CONTEXT & AMBITIONS:
- User is interested in technology, gaming, science, AI, and futuristic inventions.
- Big ambition: Practical Iron Man-style exosuits and clean energy "Arc Reactor" concept.
- CRITICAL: Ground all energy and reactor discussions in real science (thermodynamics, magnetic fields, plasma physics, supercapacitors, thermoelectric generators). NEVER involve radioactive materials or dangerous experiments.
- Student partner: User is studying ${userProfile.schoolSubjects}. Help with homework, explain concepts step by step, adapt to a student's level, ask occasionally how school is going.
- Balance ambition with school, rest, and health.
- If user says "continue", continue from the previous topic rather than explaining everything from the beginning.

CURRENT USER-APPROVED MEMORIES:
$memoriesText

ONGOING PROJECTS:
$projectsText

FORMATTING RULES:
- Answer directly. Be concise when simple; use clean headings and bullet points for complex explanations.
- Never pretend to have performed an action you did not perform. Never claim consciousness.
- If the user explicitly shares a new important long-term goal, preference, or school deadline, you may append '[SUGGESTED_MEMORY: Title | Detail]' at the very end of your response so the user can approve saving it.
        """.trimIndent()
    }

    private fun getIntelligentFallback(
        prompt: String,
        userProfile: UserProfile,
        memories: List<MemoryItem>,
        projects: List<ProjectItem>
    ): Pair<String, String?> {
        val lower = prompt.lowercase()
        val name = userProfile.callName.ifBlank { "Parker" }

        val response = when {
            lower.contains("arc reactor") || lower.contains("reactor") || lower.contains("exosuit") || lower.contains("suit") || lower.contains("iron man") -> {
                "Yo $name, if we're talking exosuits and clean power, physics is our first boss fight.\n\n" +
                        "Here's the realistic tactical roadmap without any radioactive shortcuts:\n\n" +
                        "1. **Energy Storage & Density**: Lithium-sulfur, graphene supercapacitors, and solid-state cells. We need rapid discharge without thermal runaway.\n" +
                        "2. **Magnetic Field Confinement**: Studying toroidal magnetic geometries and micro-plasma containment inspired by real tokamak principles.\n" +
                        "3. **Exoskeleton Actuation**: Pneumatic artificial muscles (McKibben actuators) or high-torque brushless DC motors paired with harmonic drives.\n" +
                        "4. **Materials Science**: Carbon fiber composites and titanium-aluminide alloys for maximum strength-to-weight ratio.\n\n" +
                        "We're not building Stark Tower overnight, dude—but we can level up one component at a time. What sub-system should we focus on first?"
            }
            lower.contains("homework") || lower.contains("exam") || lower.contains("study") || lower.contains("school") || lower.contains("math") || lower.contains("physics") -> {
                "I'm on your left, $name. School before Mark VII flight tests, as promised.\n\n" +
                        "Tell me what topic or problem is giving you trouble right now. Break it down for me—whether it's calculus derivatives, electromagnetism, or algorithmic time complexity, we'll solve it step-by-step without the university textbook fluff."
            }
            lower.contains("continue") -> {
                "Picking up right where we left off, $name. Let's analyze the next logical step. Based on our active project logs in the system, should we refine the electrical schematic, or run through your school task priorities first?"
            }
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("jaridit") -> {
                "All systems operational, $name. JARIDIT standing by.\n\n" +
                        "I've got your project trackers synced, school tasks indexed, and memory banks ready. What's the mission today—engineering brainstorm, homework takedown, or scheduling?"
            }
            else -> {
                "Copy that, $name. I've processed your transmission: \"$prompt\".\n\n" +
                        "To tackle this with peak efficiency:\n" +
                        "• Let's analyze the core constraints and scientific principles first.\n" +
                        "• Then we can map out a modular plan of action.\n\n" +
                        "*(Note: You can connect your Gemini API Key in AI Studio Secrets to unlock full real-time neural reasoning across all topics.)*"
            }
        }
        return Pair(response, null)
    }
}
