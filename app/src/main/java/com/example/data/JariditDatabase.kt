package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// --- ENTITIES ---

@Entity(tableName = "memories")
data class MemoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val detail: String,
    val category: String, // "PROFILE", "INTEREST", "PROJECT", "PREFERENCE", "SCHOOL", "FACT"
    val isSensitive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "JARIDIT"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = false,
    val suggestedMemory: String? = null
)

@Entity(tableName = "projects")
data class ProjectItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val status: String, // "ACTIVE", "RESEARCH", "PROTOTYPING", "COMPLETED"
    val progress: Int, // 0-100
    val category: String, // "SCIENCE", "ENGINEERING", "AI", "SCHOOL"
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_milestones")
data class ProjectMilestone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val title: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val taskTitle: String,
    val dueDate: String,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val isCompleted: Boolean = false,
    val notes: String = ""
)

// --- DAOS ---

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryItem>>

    @Query("SELECT * FROM memories WHERE category = :category ORDER BY createdAt DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(item: MemoryItem): Long

    @Update
    suspend fun updateMemory(item: MemoryItem)

    @Delete
    suspend fun deleteMemory(item: MemoryItem)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()

    @Query("SELECT COUNT(*) FROM memories")
    suspend fun count(): Int
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(item: ChatMessage): Long

    @Delete
    suspend fun deleteMessage(item: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectItem>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectItem): Long

    @Update
    suspend fun updateProject(project: ProjectItem)

    @Delete
    suspend fun deleteProject(project: ProjectItem)

    @Query("SELECT * FROM project_milestones WHERE projectId = :projectId ORDER BY id ASC")
    fun getMilestonesForProject(projectId: Long): Flow<List<ProjectMilestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ProjectMilestone): Long

    @Update
    suspend fun updateMilestone(milestone: ProjectMilestone)

    @Delete
    suspend fun deleteMilestone(milestone: ProjectMilestone)

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun count(): Int
}

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask): Long

    @Update
    suspend fun updateTask(task: StudyTask)

    @Delete
    suspend fun deleteTask(task: StudyTask)

    @Query("SELECT COUNT(*) FROM study_tasks")
    suspend fun count(): Int
}

// --- DATABASE ---

@Database(
    entities = [
        MemoryItem::class,
        ChatMessage::class,
        ProjectItem::class,
        ProjectMilestone::class,
        StudyTask::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JariditDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun chatDao(): ChatDao
    abstract fun projectDao(): ProjectDao
    abstract fun studyDao(): StudyDao
}
