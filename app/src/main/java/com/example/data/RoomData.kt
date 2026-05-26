package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "jovich"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "terminal_history")
data class TerminalHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val command: String,
    val response: String,
    val success: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "generated_images")
data class GeneratedImageLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface Jov1chDao {
    // Chat Queries
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // Terminal Queries
    @Query("SELECT * FROM terminal_history ORDER BY timestamp DESC LIMIT 100")
    fun getTerminalHistory(): Flow<List<TerminalHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerminalHistory(commandResult: TerminalHistory)

    @Query("DELETE FROM terminal_history")
    suspend fun clearTerminalHistory()

    // Image Log Queries
    @Query("SELECT * FROM generated_images ORDER BY timestamp DESC")
    fun getGeneratedImages(): Flow<List<GeneratedImageLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedImage(imageLog: GeneratedImageLog)
}

@Database(
    entities = [ChatMessage::class, TerminalHistory::class, GeneratedImageLog::class],
    version = 1,
    exportSchema = false
)
abstract class Jov1chDatabase : RoomDatabase() {
    abstract val dao: Jov1chDao
}
