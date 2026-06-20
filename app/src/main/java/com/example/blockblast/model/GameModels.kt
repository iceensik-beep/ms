package com.example.blockblast.model

import androidx.compose.ui.graphics.Color

data class Coordinate(val row: Int, val col: Int) {
    operator fun plus(other: Coordinate) = Coordinate(this.row + other.row, this.col + other.col)
}

enum class CellType {
    EMPTY,
    FILLED,
    OBSTACLE,  // Solid brick obstacle pre-placed in campaign levels - breaks when line is cleared
    FROZEN    // Ice block - requires clearing twice (first clear melts it to standard filled block, second completely clears it)
}

data class GameCell(
    val type: CellType = CellType.EMPTY,
    val color: Color = Color.Transparent,
    val hasShatterEffect: Boolean = false // Track when recently broken for flash animations
)

enum class GameMode {
    CLASSIC,
    CAMPAIGN
}

data class BlockShape(
    val id: String,
    val coordinates: List<Coordinate>,
    val color: Color,
    val name: String,
    val width: Int,
    val height: Int
) {
    // Return coordinates adjusted to top-left aligned bounding box
    fun getNormalizedCoordinates(): List<Coordinate> {
        if (coordinates.isEmpty()) return emptyList()
        val minRow = coordinates.minOf { it.row }
        val minCol = coordinates.minOf { it.col }
        return coordinates.map { Coordinate(it.row - minRow, it.col - minCol) }
    }
}

// Global Preset Templates for shapes
object ShapeTemplates {
    val COLORS = listOf(
        Color(0xFF03DAC6), // Cyan Neon
        Color(0xFFBB86FC), // Purple Neon
        Color(0xFFCF6679), // Light Coral Red
        Color(0xFFF48FB1), // Soft Pink Hot
        Color(0xFFFFB703), // Gold Yellow
        Color(0xFF8B5CF6), // Dream Violet
        Color(0xFF38BDF8), // Light Sky Blue
        Color(0xFF10B981)  // Emerald Green
    )

    fun createShape(name: String, coords: List<Coordinate>, color: Color): BlockShape {
        val minRow = coords.minOf { it.row }
        val maxRow = coords.maxOf { it.row }
        val minCol = coords.minOf { it.col }
        val maxCol = coords.maxOf { it.col }
        return BlockShape(
            id = java.util.UUID.randomUUID().toString(),
            coordinates = coords,
            color = color,
            name = name,
            width = maxCol - minCol + 1,
            height = maxRow - minRow + 1
        )
    }

    fun generateRandomShapes(count: Int = 3): List<BlockShape> {
        val list = mutableListOf<BlockShape>()
        val possibleShapes = listOf(
            // Single Dot
            "Dot" to listOf(Coordinate(0, 0)),
            
            // Duo horizontal / vertical
            "DuoH" to listOf(Coordinate(0, 0), Coordinate(0, 1)),
            "DuoV" to listOf(Coordinate(0, 0), Coordinate(1, 0)),
            
            // Trio lines
            "TrioH" to listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2)),
            "TrioV" to listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0)),
            
            // Tetra lines
            "TetraH" to listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2), Coordinate(0, 3)),
            "TetraV" to listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0), Coordinate(3, 0)),

            // Square 2x2
            "Square2x2" to listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(1, 0), Coordinate(1, 1)),
            
            // L Shapes (Standard & rotated)
            "L_Normal" to listOf(Coordinate(0,0), Coordinate(1,0), Coordinate(2,0), Coordinate(2,1)),
            "L_Right" to listOf(Coordinate(0,0), Coordinate(0,1), Coordinate(0,2), Coordinate(1,2)),
            "L_Inverted" to listOf(Coordinate(0,1), Coordinate(1,1), Coordinate(2,1), Coordinate(2,0)),
            "L_Rotated" to listOf(Coordinate(0,0), Coordinate(1,0), Coordinate(1,1), Coordinate(1,2)),

            // Mini L Corner (3 blocks)
            "MiniCorner" to listOf(Coordinate(0,0), Coordinate(1,0), Coordinate(1,1)),
            "MiniCorner2" to listOf(Coordinate(0,1), Coordinate(1,1), Coordinate(1,0)),
            
            // T Shapes
            "T_Shape" to listOf(Coordinate(0,0), Coordinate(0,1), Coordinate(0,2), Coordinate(1,1)),
            "T_Rot" to listOf(Coordinate(0,1), Coordinate(1,0), Coordinate(1,1), Coordinate(2,1)),

            // Z & S shapes
            "Z_Shape" to listOf(Coordinate(0,0), Coordinate(0,1), Coordinate(1,1), Coordinate(1,2)),
            "S_Shape" to listOf(Coordinate(0,1), Coordinate(0,2), Coordinate(1,0), Coordinate(1,1))
        )

        for (i in 0 until count) {
            val element = possibleShapes.random()
            val color = COLORS.random()
            list.add(createShape(element.first, element.second, color))
        }
        return list
    }
}

data class LevelData(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val targetScore: Int,
    val maxMoves: Int?, // Optional limit
    val rewardCoins: Int,
    val obstacles: List<Pair<Coordinate, CellType>> = emptyList() // Preplaced bricks or ice
)

object LevelPreset {
    val levels = listOf(
        LevelData(
            levelNumber = 1,
            title = "Основы Block Blast",
            description = "Очистите линии, чтобы набрать 500 очков!",
            targetScore = 500,
            maxMoves = null,
            rewardCoins = 50
        ),
        LevelData(
            levelNumber = 2,
            title = "Первые преграды",
            description = "Разбивайте серые блоки-препятствия, заполняя строки на уровне!",
            targetScore = 800,
            maxMoves = 25,
            rewardCoins = 100,
            obstacles = listOf(
                Coordinate(3, 3) to CellType.OBSTACLE,
                Coordinate(3, 4) to CellType.OBSTACLE,
                Coordinate(4, 3) to CellType.OBSTACLE,
                Coordinate(4, 4) to CellType.OBSTACLE
            )
        ),
        LevelData(
            levelNumber = 3,
            title = "Ледяной замок",
            description = "Очистите ряды с ледяными блоками. Лёд ломается в обычный блок с первого захода!",
            targetScore = 1200,
            maxMoves = 30,
            rewardCoins = 150,
            obstacles = listOf(
                Coordinate(2, 2) to CellType.FROZEN,
                Coordinate(2, 5) to CellType.FROZEN,
                Coordinate(5, 2) to CellType.FROZEN,
                Coordinate(5, 5) to CellType.FROZEN,
                Coordinate(3, 3) to CellType.OBSTACLE,
                Coordinate(4, 4) to CellType.OBSTACLE
            )
        ),
        LevelData(
            levelNumber = 4,
            title = "Круговой барьер",
            description = "Множество препятствий! Используйте бомбу или молоток в случае застревания.",
            targetScore = 1500,
            maxMoves = 35,
            rewardCoins = 200,
            obstacles = listOf(
                Coordinate(1, 1) to CellType.OBSTACLE,
                Coordinate(1, 6) to CellType.OBSTACLE,
                Coordinate(6, 1) to CellType.OBSTACLE,
                Coordinate(6, 6) to CellType.OBSTACLE,
                Coordinate(3, 2) to CellType.FROZEN,
                Coordinate(3, 5) to CellType.FROZEN,
                Coordinate(4, 2) to CellType.FROZEN,
                Coordinate(4, 5) to CellType.FROZEN
            )
        ),
        LevelData(
            levelNumber = 5,
            title = "Супер Испытание",
            description = "Сложный уровень. Наберите 2200 очков за ограниченные ходы!",
            targetScore = 2200,
            maxMoves = 40,
            rewardCoins = 300,
            obstacles = listOf(
                Coordinate(0, 0) to CellType.OBSTACLE,
                Coordinate(7, 7) to CellType.OBSTACLE,
                Coordinate(0, 7) to CellType.OBSTACLE,
                Coordinate(7, 0) to CellType.OBSTACLE,
                Coordinate(3, 3) to CellType.FROZEN,
                Coordinate(3, 4) to CellType.FROZEN,
                Coordinate(4, 3) to CellType.FROZEN,
                Coordinate(4, 4) to CellType.FROZEN,
                Coordinate(2, 2) to CellType.OBSTACLE,
                Coordinate(5, 5) to CellType.OBSTACLE
            )
        )
    )

    fun getLevel(num: Int): LevelData {
        val base = levels.find { it.levelNumber == num }
        if (base != null) return base
        // Procedurally generate level for very high numbers
        return LevelData(
            levelNumber = num,
            title = "Этап $num",
            description = "Усложненная проходочка с препятствиями! Очки: ${1500 + num * 200}",
            targetScore = 1500 + num * 200,
            maxMoves = 30 + (num % 5) * 3,
            rewardCoins = 100 + num * 20,
            obstacles = List(minOf(num + 3, 15)) {
                val row = (0..7).random()
                val col = (0..7).random()
                val type = if (Math.random() > 0.5) CellType.OBSTACLE else CellType.FROZEN
                Coordinate(row, col) to type
            }.distinctBy { it.first }
        )
    }
}
