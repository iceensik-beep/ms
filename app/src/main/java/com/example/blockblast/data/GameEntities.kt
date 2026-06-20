package com.example.blockblast.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_stats")
data class GameStats(
    @PrimaryKey val id: Int = 1, // Singleton row
    val highScore: Int = 0,
    val totalCoins: Int = 200, // Starts with some default helper coins
    val unlockedLevels: Int = 1
)

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val levelId: Int,
    val stars: Int = 0,
    val score: Int = 0,
    val completed: Boolean = false
)

@Dao
interface GameDao {
    @Query("SELECT * FROM game_stats WHERE id = 1 LIMIT 1")
    fun getGameStatsFlow(): Flow<GameStats?>

    @Query("SELECT * FROM game_stats WHERE id = 1 LIMIT 1")
    suspend fun getGameStats(): GameStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameStats(stats: GameStats)

    @Query("SELECT * FROM level_progress")
    fun getAllLevelProgressFlow(): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    suspend fun getLevelProgress(levelId: Int): LevelProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(level: LevelProgress)
}
