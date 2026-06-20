package com.example.blockblast.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    val gameStats: Flow<GameStats> = gameDao.getGameStatsFlow().map { it ?: GameStats() }
    val levelProgressList: Flow<List<LevelProgress>> = gameDao.getAllLevelProgressFlow()

    suspend fun getOrInitStats(): GameStats = withContext(Dispatchers.IO) {
        var stats = gameDao.getGameStats()
        if (stats == null) {
            stats = GameStats()
            gameDao.insertGameStats(stats)
        }
        stats
    }

    suspend fun updateHighScore(newScore: Int) = withContext(Dispatchers.IO) {
        val stats = getOrInitStats()
        if (newScore > stats.highScore) {
            gameDao.insertGameStats(stats.copy(highScore = newScore))
        }
    }

    suspend fun addCoins(amount: Int) = withContext(Dispatchers.IO) {
        val stats = getOrInitStats()
        gameDao.insertGameStats(stats.copy(totalCoins = stats.totalCoins + amount))
    }

    suspend fun spendCoins(amount: Int): Boolean = withContext(Dispatchers.IO) {
        val stats = getOrInitStats()
        if (stats.totalCoins >= amount) {
            gameDao.insertGameStats(stats.copy(totalCoins = stats.totalCoins - amount))
            true
        } else {
            false
        }
    }

    suspend fun unlockLevel(levelId: Int) = withContext(Dispatchers.IO) {
        val stats = getOrInitStats()
        if (levelId > stats.unlockedLevels) {
            gameDao.insertGameStats(stats.copy(unlockedLevels = levelId))
        }
    }

    suspend fun saveLevelProgress(levelId: Int, score: Int, stars: Int) = withContext(Dispatchers.IO) {
        val currentProgress = gameDao.getLevelProgress(levelId)
        val maxScore = maxOf(score, currentProgress?.score ?: 0)
        val maxStars = maxOf(stars, currentProgress?.stars ?: 0)
        
        gameDao.insertLevelProgress(
            LevelProgress(
                levelId = levelId,
                score = maxScore,
                stars = maxStars,
                completed = true
            )
        )
        // Also unlock the next level
        unlockLevel(levelId + 1)
    }
}
