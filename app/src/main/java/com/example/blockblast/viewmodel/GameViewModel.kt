package com.example.blockblast.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.blockblast.data.GameRepository
import com.example.blockblast.data.GameStats
import com.example.blockblast.data.LevelProgress
import com.example.blockblast.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PowerUpType {
    NONE, HAMMER, BOMB, ROTATE
}

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Persistent database flows
    val gameStatsFLow: StateFlow<GameStats> = repository.gameStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameStats())

    val levelProgressListFlow: StateFlow<List<LevelProgress>> = repository.levelProgressList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Game Session Variables
    var gridState by mutableStateOf<List<List<GameCell>>>(
        List(8) { List(8) { GameCell() } }
    )
        private set

    var activeChoices by mutableStateOf<List<BlockShape?>>(
        listOf(null, null, null)
    )
        private set

    var selectedShapeIndex by mutableStateOf(-1)
    
    var currentScore by mutableStateOf(0)
        private set

    var comboCount by mutableStateOf(0)
        private set

    var movesRemaining by mutableStateOf<Int?>(null)
        private set

    var activeMode by mutableStateOf(GameMode.CLASSIC)
        private set

    var currentLevelNumber by mutableStateOf(1)
        private set

    var activePowerUp by mutableStateOf(PowerUpType.NONE)
        private set

    // Game screens states
    var isGameOver by mutableStateOf(false)
        private set

    var isLevelCompleted by mutableStateOf(false)
        private set

    var scoreIncrementPopup by mutableStateOf<String?>(null)
        private set

    init {
        // Pre-initialize stats if first run
        viewModelScope.launch {
            repository.getOrInitStats()
        }
    }

    // Initialize/Start a fresh Classic endless game
    fun startClassicGame() {
        gridState = List(8) { List(8) { GameCell() } }
        activeChoices = ShapeTemplates.generateRandomShapes(3)
        selectedShapeIndex = -1
        currentScore = 0
        comboCount = 0
        movesRemaining = null
        activeMode = GameMode.CLASSIC
        activePowerUp = PowerUpType.NONE
        isGameOver = false
        isLevelCompleted = false
    }

    // Initialize/Start a campaign Level
    fun startCampaignLevel(levelNum: Int) {
        val level = LevelPreset.getLevel(levelNum)
        currentLevelNumber = levelNum
        activeMode = GameMode.CAMPAIGN
        currentScore = 0
        comboCount = 0
        movesRemaining = level.maxMoves
        activePowerUp = PowerUpType.NONE
        isGameOver = false
        isLevelCompleted = false
        selectedShapeIndex = -1

        // Initialize grid with custom obstacles/iced bricks
        val baseGrid = MutableList(8) { MutableList(8) { GameCell() } }
        level.obstacles.forEach { (coord, type) ->
            if (coord.row in 0..7 && coord.col in 0..7) {
                val color = when (type) {
                    CellType.OBSTACLE -> Color(0xFF6C757D) // Heavy Stone Gray
                    CellType.FROZEN -> Color(0xFF90E0EF)   // Sky Icy Indigo
                    else -> Color.Transparent
                }
                baseGrid[coord.row][coord.col] = GameCell(type = type, color = color)
            }
        }
        gridState = baseGrid.map { it.toList() }
        activeChoices = ShapeTemplates.generateRandomShapes(3)
    }

    // Rotate powerup selection
    fun activateRotatePowerUp() {
        if (activePowerUp == PowerUpType.ROTATE) {
            activePowerUp = PowerUpType.NONE
            return
        }
        val cost = 20
        val currentCoins = gameStatsFLow.value.totalCoins
        if (currentCoins >= cost) {
            activePowerUp = PowerUpType.ROTATE
        } else {
            // Cannot trigger, not enough coins
        }
    }

    // Trigger shape rotation (spends 20 coins and rotates selected bottom piece)
    fun rotateShapeAtIndex(index: Int) {
        val shape = activeChoices[index] ?: return
        val currentCoins = gameStatsFLow.value.totalCoins
        if (currentCoins < 20) return

        viewModelScope.launch {
            val wasSpent = repository.spendCoins(20)
            if (wasSpent) {
                // Perform 90 degree rotation clockwise
                val rotatedCoords = shape.coordinates.map {
                    // (r, c) -> (c, -r)
                    Coordinate(it.col, -it.row)
                }
                // Normalize to top-left aligned bounding box
                val minR = rotatedCoords.minOf { it.row }
                val minC = rotatedCoords.minOf { it.col }
                val normalized = rotatedCoords.map { Coordinate(it.row - minR, it.col - minC) }

                val maxR = normalized.maxOf { it.row }
                val maxC = normalized.maxOf { it.col }

                activeChoices = activeChoices.toMutableList().apply {
                    this[index] = shape.copy(
                        coordinates = normalized,
                        width = maxC + 1,
                        height = maxR + 1
                    )
                }
                activePowerUp = PowerUpType.NONE
                selectedShapeIndex = -1
                
                // Rotation might have saved the game or changed fit states, check game over
                checkGameStatus()
            }
        }
    }

    // Trigger Shuffle powerup (Shuffles all 3 pieces for 30 coins)
    fun triggerShuffle() {
        val cost = 30
        val currentCoins = gameStatsFLow.value.totalCoins
        if (currentCoins < cost) return

        viewModelScope.launch {
            val spent = repository.spendCoins(cost)
            if (spent) {
                // Find indexes that are not null and generate for them
                activeChoices = activeChoices.map { shape ->
                    if (shape != null) ShapeTemplates.generateRandomShapes(1).first() else null
                }
                // Clear state selection
                selectedShapeIndex = -1
                activePowerUp = PowerUpType.NONE
                checkGameStatus()
            }
        }
    }

    // Toggle Hammer powerup selection (Lets player break single tile for 40 coins)
    fun toggleHammerPowerUp() {
        if (activePowerUp == PowerUpType.HAMMER) {
            activePowerUp = PowerUpType.NONE
        } else {
            val cost = 40
            if (gameStatsFLow.value.totalCoins >= cost) {
                activePowerUp = PowerUpType.HAMMER
            }
        }
    }

    // Toggle Bomb powerup selection (Clears 3x3 for 80 coins)
    fun toggleBombPowerUp() {
        if (activePowerUp == PowerUpType.BOMB) {
            activePowerUp = PowerUpType.NONE
        } else {
            val cost = 80
            if (gameStatsFLow.value.totalCoins >= cost) {
                activePowerUp = PowerUpType.BOMB
            }
        }
    }

    // Execute active Hammer tile click
    private fun executeHammer(row: Int, col: Int) {
        val cell = gridState[row][col]
        if (cell.type == CellType.EMPTY) return

        viewModelScope.launch {
            val spent = repository.spendCoins(40)
            if (spent) {
                val updated = gridState.map { it.toMutableList() }
                updated[row][col] = GameCell(type = CellType.EMPTY, color = cell.color, hasShatterEffect = true)
                gridState = updated.map { it.toList() }
                
                // Clear visual shatter state after a brief animation duration
                delay(300)
                resetShatterEffect(listOf(Coordinate(row, col)))
                
                activePowerUp = PowerUpType.NONE
                checkGameStatus()
            }
        }
    }

    // Execute active Bomb 3x3 explosion
    private fun executeBomb(row: Int, col: Int) {
        viewModelScope.launch {
            val spent = repository.spendCoins(80)
            if (spent) {
                val updated = gridState.map { it.toMutableList() }
                val affectedCoordinates = mutableListOf<Coordinate>()

                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val r = row + dr
                        val c = col + dc
                        if (r in 0..7 && c in 0..7) {
                            val activeCell = updated[r][c]
                            if (activeCell.type != CellType.EMPTY) {
                                updated[r][c] = GameCell(
                                    type = CellType.EMPTY,
                                    color = activeCell.color,
                                    hasShatterEffect = true
                                )
                                affectedCoordinates.add(Coordinate(r, c))
                            }
                        }
                    }
                }

                gridState = updated.map { it.toList() }
                
                // Clear visual shatter state
                delay(300)
                resetShatterEffect(affectedCoordinates)

                activePowerUp = PowerUpType.NONE
                checkGameStatus()
            }
        }
    }

    private fun resetShatterEffect(coords: List<Coordinate>) {
        val updated = gridState.map { it.toMutableList() }
        coords.forEach { coord ->
            if (coord.row in 0..7 && coord.col in 0..7) {
                val cell = updated[coord.row][coord.col]
                updated[coord.row][coord.col] = cell.copy(hasShatterEffect = false)
            }
        }
        gridState = updated.map { it.toList() }
    }

    // Generic grid tap: Routes to normal placement or active board power-ups
    fun onCellTapped(row: Int, col: Int) {
        if (isGameOver || isLevelCompleted) return

        if (activePowerUp == PowerUpType.HAMMER) {
            executeHammer(row, col)
            return
        }
        if (activePowerUp == PowerUpType.BOMB) {
            executeBomb(row, col)
            return
        }

        // Standard Block Blast tap placement logic
        val shapeIndex = selectedShapeIndex
        if (shapeIndex < 0 || shapeIndex >= activeChoices.size) return
        val shape = activeChoices[shapeIndex] ?: return

        if (tryPlaceShape(shape, row, col)) {
            // Placement was successful! Clear selection
            val nextChoices = activeChoices.toMutableList()
            nextChoices[shapeIndex] = null
            activeChoices = nextChoices
            selectedShapeIndex = -1

            // Count down moves in Campaign limits
            movesRemaining?.let {
                movesRemaining = maxOf(0, it - 1)
            }

            // If all 3 slots placement completed, auto spawn 3 fresh ones
            if (activeChoices.all { it == null }) {
                activeChoices = ShapeTemplates.generateRandomShapes(3)
            }

            // Sweep and destroy filled lines
            checkForCombosAndClear()
        }
    }

    // Checks if shape fits on the grid at row, col offset (with that cell as top-left anchor)
    fun canShapeFitAt(shape: BlockShape, startRow: Int, startCol: Int): Boolean {
        val normalized = shape.getNormalizedCoordinates()
        for (coord in normalized) {
            val targetRow = startRow + coord.row
            val targetCol = startCol + coord.col
            if (targetRow !in 0..7 || targetCol !in 0..7) return false
            if (gridState[targetRow][targetCol].type != CellType.EMPTY) return false
        }
        return true
    }

    // Standard Block Blast placement fit execution
    private fun tryPlaceShape(shape: BlockShape, startRow: Int, startCol: Int): Boolean {
        if (!canShapeFitAt(shape, startRow, startCol)) return false

        val updatedGrid = gridState.map { it.toMutableList() }
        val normalized = shape.getNormalizedCoordinates()

        for (coord in normalized) {
            val targetRow = startRow + coord.row
            val targetCol = startCol + coord.col
            updatedGrid[targetRow][targetCol] = GameCell(
                type = CellType.FILLED,
                color = shape.color
            )
        }

        // Set points for placing block tiles (+10 points for each unit block tile placed)
        val addedPoints = normalized.size * 10
        currentScore += addedPoints
        scoreIncrementPopup = "+$addedPoints"

        gridState = updatedGrid.map { it.toList() }
        return true
    }

    // Clears score popup after short duration
    fun clearScorePopup() {
        scoreIncrementPopup = null
    }

    // Scan the lines (Rows/Cols), melt iced blocks and shatter completed rows/columns
    private fun checkForCombosAndClear() {
        val rowsToClear = mutableListOf<Int>()
        val colsToClear = mutableListOf<Int>()

        // 1. Scan rows
        for (r in 0..7) {
            var fullCount = 0
            for (c in 0..7) {
                if (gridState[r][c].type != CellType.EMPTY) fullCount++
            }
            if (fullCount == 8) {
                rowsToClear.add(r)
            }
        }

        // 2. Scan columns
        for (c in 0..7) {
            var fullCount = 0
            for (r in 0..7) {
                if (gridState[r][c].type != CellType.EMPTY) fullCount++
            }
            if (fullCount == 8) {
                colsToClear.add(c)
            }
        }

        val totalLines = rowsToClear.size + colsToClear.size

        if (totalLines > 0) {
            // We have a combo match!
            comboCount++
            val baseLinePoints = when (totalLines) {
                1 -> 100
                2 -> 300
                3 -> 600
                else -> 1000 // Multi line blast bonus!
            }
            val comboBonus = (1.0 + (comboCount - 1) * 0.5)
            val lineBlastPoints = (baseLinePoints * comboBonus).toInt()

            currentScore += lineBlastPoints
            scoreIncrementPopup = "BLAST x$totalLines! +$lineBlastPoints"

            // Compute board modifications
            val updated = gridState.map { it.toMutableList() }
            val cellsToShatter = mutableListOf<Coordinate>()

            // Melt ICE or completely break normal/obstacle cells
            // For columns
            colsToClear.forEach { c ->
                for (r in 0..7) {
                    val cell = updated[r][c]
                    if (cell.type == CellType.FROZEN) {
                        // Frost melts down to standard filled block, and isn't deleted yet
                        updated[r][c] = cell.copy(type = CellType.FILLED, color = Color(0xFF5390D9))
                    } else if (cell.type != CellType.EMPTY) {
                        updated[r][c] = GameCell(type = CellType.EMPTY, color = cell.color, hasShatterEffect = true)
                        cellsToShatter.add(Coordinate(r, c))
                    }
                }
            }

            // For rows
            rowsToClear.forEach { r ->
                for (c in 0..7) {
                    val cell = updated[r][c]
                    if (cell.type == CellType.FROZEN) {
                        updated[r][c] = cell.copy(type = CellType.FILLED, color = Color(0xFF5390D9))
                    } else if (cell.type != CellType.EMPTY) {
                        updated[r][c] = GameCell(type = CellType.EMPTY, color = cell.color, hasShatterEffect = true)
                        cellsToShatter.add(Coordinate(r, c))
                    }
                }
            }

            gridState = updated.map { it.toList() }

            // Clear visual shatter state after delay
            viewModelScope.launch {
                delay(400)
                resetShatterEffect(cellsToShatter)
                checkGameStatus()
            }
        } else {
            // Combo break
            comboCount = 0
            checkGameStatus()
        }
    }

    // Verify endgame bounds, moves depleted or level criteria matched
    private fun checkGameStatus() {
        if (activeMode == GameMode.CAMPAIGN) {
            val level = LevelPreset.getLevel(currentLevelNumber)
            
            // Victory target score matched? Let's check!
            if (currentScore >= level.targetScore) {
                isLevelCompleted = true
                viewModelScope.launch {
                    // Give stars based on performance (e.g. 1, 2, or 3 based on speed / leftover moves)
                    val stars = when {
                        movesRemaining == null -> 3
                        (movesRemaining ?: 0) >= (level.maxMoves ?: 30) / 2 -> 3
                        (movesRemaining ?: 0) >= (level.maxMoves ?: 30) / 4 -> 2
                        else -> 1
                    }
                    repository.saveLevelProgress(currentLevelNumber, currentScore, stars)
                    repository.addCoins(level.rewardCoins)
                }
                return
            }

            // Out of moves checked?
            movesRemaining?.let {
                if (it <= 0) {
                    isGameOver = true
                    return
                }
            }
        }

        // Check is fits exist for ANY of the active shape choices
        val activeShapes = activeChoices.filterNotNull()
        if (activeShapes.isNotEmpty()) {
            var fitExists = false
            for (shape in activeShapes) {
                if (anyGridFittingCoordsExist(shape)) {
                    fitExists = true
                    break
                }
            }
            if (!fitExists) {
                // Game over! No shape fits anywhere on the remaining spaces
                isGameOver = true
                if (activeMode == GameMode.CLASSIC) {
                    viewModelScope.launch {
                        repository.updateHighScore(currentScore)
                    }
                }
            }
        }
    }

    // Loops the total board spaces to see if a shape can be placed anywhere
    private fun anyGridFittingCoordsExist(shape: BlockShape): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                if (canShapeFitAt(shape, r, c)) {
                    return true
                }
            }
        }
        return false
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
