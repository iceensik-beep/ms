package com.example.blockblast.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.blockblast.data.LevelProgress
import com.example.blockblast.model.BlockShape
import com.example.blockblast.model.CellType
import com.example.blockblast.model.GameMode
import com.example.blockblast.model.LevelPreset
import com.example.blockblast.viewmodel.GameViewModel
import com.example.blockblast.viewmodel.PowerUpType
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onStartClassic: () -> Unit,
    onStartCampaign: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.gameStatsFLow.collectAsStateWithLifecycle()
    val levelsCompleted by viewModel.levelProgressListFlow.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Decorative Retro Space Grid Backdrop matching high-contrast slate
        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 10
            val rows = 15
            val dx = size.width / columns
            val dy = size.height / rows

            // Vertical lines
            for (i in 0..columns) {
                drawLine(
                    color = Color(0x08FFFFFF),
                    start = Offset(i * dx, 0f),
                    end = Offset(i * dx, size.height),
                    strokeWidth = 1.2f
                )
            }
            // Horizontal lines
            for (i in 0..rows) {
                drawLine(
                    color = Color(0x08FFFFFF),
                    start = Offset(0f, i * dy),
                    end = Offset(size.width, i * dy),
                    strokeWidth = 1.2f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Game main title with Bold Typography styles
            Text(
                text = "BLOCK BLAST",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                color = Color(0xFFE0E0E0),
                letterSpacing = (-1.5).sp
            )
            Text(
                text = "ADVENTURE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFBB86FC),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Stats Card representing Highscore & Balance in Bold Typography style (#2A2D35 container)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D35)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Рекорд",
                                tint = Color(0xFFBB86FC),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "РЕКОРД ИГРЫ",
                                fontSize = 10.sp,
                                color = Color(0xFFB3B3B3),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${stats.highScore}",
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Identifier Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Монеты",
                                tint = Color(0xFF03DAC6),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "БАЛАНС МОНЕТ",
                                fontSize = 10.sp,
                                color = Color(0xFFB3B3B3),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${stats.totalCoins}",
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Play Classic Button (Endless Mode) - High contrast bold typography layout block button
            Button(
                onClick = onStartClassic,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03DAC6),
                    contentColor = Color(0xFF121212)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Старт",
                        modifier = Modifier.size(26.dp),
                        tint = Color(0xFF121212)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "БЕСКОНЕЧНЫЙ РЕЖИМ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            // Campaign Levels Header with Bold Typography theme
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "adventure",
                    tint = Color(0xFFBB86FC),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ПРИКЛЮЧЕНИЕ (УРОВНИ)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Horizontal scrolling gorgeous Level Path Node Map
            val totalLevelsAvailable = 10
            val unlockedLevelLimit = stats.unlockedLevels

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items((1..totalLevelsAvailable).toList()) { levelNum ->
                    val isUnlocked = levelNum <= unlockedLevelLimit
                    val progressInfo = levelsCompleted.find { it.levelId == levelNum }
                    val starsCount = progressInfo?.stars ?: 0
                    val isCompleted = progressInfo?.completed ?: false

                    LevelPathNode(
                        levelNumber = levelNum,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        stars = starsCount,
                        onClick = {
                            if (isUnlocked) {
                                onStartCampaign(levelNum)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Game Play Instructions - Styled as Bold Typography card (#2A2D35)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D35)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📖 ПРАВИЛА ИГРЫ:",
                        color = Color(0xFF03DAC6),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "1. Выбирайте блоки внизу экрана и ставьте их на сетку 8х8.\n" +
                               "2. Заполняйте строки и столбцы полностью, чтобы взрывать их и получать очки.\n" +
                               "3. Очищайте несколько линий одновременно для комбо-множителей!\n" +
                               "4. Используйте мощные бонусы (Молоток, Бомба, Ротация, Шаффл) за монеты.\n" +
                               "5. В кампании разбивайте каменное препятствие и лед (лед требует двух взрывов)!",
                        color = Color(0xFFB3B3B3),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LevelPathNode(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val presetDescription = if (levelNumber <= 5) {
        LevelPreset.getLevel(levelNumber).title
    } else {
        "Этап $levelNumber"
    }

    val glowAlpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Card(
        modifier = Modifier
            .width(135.dp)
            .clickable(enabled = isUnlocked, onClick = onClick)
            .scale(if (isUnlocked) 1f else 0.95f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> Color(0x3B03DAC6)
                isUnlocked -> Color(0x3BBB86FC)
                else -> Color(0xFF1E1E1E)
            }
        ),
        border = BorderStroke(
            1.5.dp,
            when {
                isCompleted -> Color(0xFF03DAC6)
                isUnlocked -> Color(0xFFBB86FC).copy(alpha = glowAlpha)
                else -> Color(0xFF2A2D35)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when {
                            isCompleted -> Color(0xFF03DAC6)
                            isUnlocked -> Color(0xFFBB86FC)
                            else -> Color(0xFF2A2D35)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = "$levelNumber",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCompleted) Color(0xFF121212) else Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "locked",
                        tint = Color(0xFFB3B3B3),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = presetDescription,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isUnlocked) Color.White else Color(0xFFB3B3B3),
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Display Stars in Theme colors
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (i in 1..3) {
                    val starActive = i <= stars
                    Icon(
                        imageVector = if (starActive) Icons.Filled.Star else Icons.Default.Star,
                        contentDescription = "star",
                        tint = if (starActive) Color(0xFFFFB703) else Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.gameStatsFLow.collectAsStateWithLifecycle()
    
    // Core states
    val grid = viewModel.gridState
    val choices = viewModel.activeChoices
    val selectedIndex = viewModel.selectedShapeIndex
    val currentScore = viewModel.currentScore
    val combo = viewModel.comboCount
    val moves = viewModel.movesRemaining
    val mode = viewModel.activeMode
    val activePowerUp = viewModel.activePowerUp
    val levelNum = viewModel.currentLevelNumber
    val isGameOver = viewModel.isGameOver
    val isLevelCompleted = viewModel.isLevelCompleted

    val currentLevelPreset = if (mode == GameMode.CAMPAIGN) LevelPreset.getLevel(levelNum) else null

    val density = LocalDensity.current
    var rootLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var boardLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val shapeSlotCoordinates = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
    
    var activeDragIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragPositionOnScreen by remember { mutableStateOf(Offset.Zero) }

    val hoveredRowCol by remember(activeDragIndex, dragPositionOnScreen, boardLayoutCoordinates) {
        derivedStateOf {
            val boardCoords = boardLayoutCoordinates
            if (activeDragIndex == -1 || boardCoords == null) return@derivedStateOf null
            val shape = choices.getOrNull(activeDragIndex) ?: return@derivedStateOf null
            
            // Adjust pointer location upwards slightly (tactile offset) so finger doesn't block blocks!
            val verticalTactileOffsetPx = with(density) { -55.dp.toPx() }
            val adjustedPointerPosInWindow = dragPositionOnScreen + Offset(0f, verticalTactileOffsetPx)
            
            val localBoardOffset = boardCoords.windowToLocal(adjustedPointerPosInWindow)
            val boardW = boardCoords.size.width.toFloat()
            val boardH = boardCoords.size.height.toFloat()
            val cellW = boardW / 8f
            val cellH = boardH / 8f
            
            // Align start from top-left block offset derived from shapes dimensions
            val startCol = ((localBoardOffset.x - (shape.width - 1) * cellW / 2f) / cellW).roundToInt()
            val startRow = ((localBoardOffset.y - (shape.height - 1) * cellH / 2f) / cellH).roundToInt()
            
            if (startRow in -2..9 && startCol in -2..9) {
                Pair(startRow, startCol)
            } else {
                null
            }
        }
    }

    // Glow animation for selection / powerup
    val borderAnimAlpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

       // Layout with Bold Typography style (#121111 background)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .windowInsetsPadding(WindowInsets.statusBars)
            .onGloballyPositioned { rootLayoutCoordinates = it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Action Buttons Row styled to theme
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToMenu,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF2A2D35)),
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Mode Info Title with Bold typography styles
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (mode == GameMode.CLASSIC) "БЕСКОНЕЧНЫЙ" else "${currentLevelPreset?.title?.uppercase()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (mode == GameMode.CLASSIC) Color(0xFF03DAC6) else Color(0xFFBB86FC),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    )
                    if (mode == GameMode.CAMPAIGN) {
                        Text(
                            text = "ЦЕЛЬ: ${currentLevelPreset?.targetScore} ОЧКОВ",
                            fontSize = 10.sp,
                            color = Color(0xFFB3B3B3),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Coins balance badge styled to theme
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A2D35))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "coins",
                        tint = Color(0xFF03DAC6),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stats.totalCoins}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score Dashboard Area with big, tight Bold Typography layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Score
                Column {
                    Text(
                        text = "ОЧКИ ИГРЫ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB3B3B3),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$currentScore",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE0E0E0),
                        letterSpacing = (-1).sp
                    )
                }

                // Moves counter helper (Available in Campaign limits only)
                if (mode == GameMode.CAMPAIGN && moves != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ОСТАЛОСЬ ХОДОВ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCF6679),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$moves",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = if (moves <= 5) Color(0xFFCF6679) else Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                } else {
                    // Classic Mode: High Score reference display as a badge
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .background(Color(0xFF2A2D35), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "РЕКОРД",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFBB86FC),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${maxOf(stats.highScore, currentScore)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFBB86FC)
                        )
                    }
                }
            }

            // Beautiful Campaign Level Progress Bar towards Target score
            if (mode == GameMode.CAMPAIGN && currentLevelPreset != null) {
                val progressFraction = minOf(1.5f, currentScore.toFloat() / currentLevelPreset.targetScore.toFloat())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 10.dp, start = 8.dp, end = 8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { minOf(1f, progressFraction) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = Color(0xFFBB86FC),
                        trackColor = Color(0xFF2A2D35)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Прогресс: ${(progressFraction * 100).toInt()}%",
                            fontSize = 10.sp,
                            color = Color(0xFFB3B3B3),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$currentScore / ${currentLevelPreset.targetScore}",
                            fontSize = 10.sp,
                            color = Color(0xFFB3B3B3),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Combo & Blow popup notifier overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.scoreIncrementPopup != null,
                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB703)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = viewModel.scoreIncrementPopup ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF121212),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                    LaunchedEffect(viewModel.scoreIncrementPopup) {
                        delay(2000)
                        viewModel.clearScorePopup()
                    }
                }

                // If Combo multiplier sits high!
                if (combo > 1 && viewModel.scoreIncrementPopup == null) {
                    Text(
                        text = "КОМБО КРАШ x$combo! 🔥",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFB703),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Puzzle Game Board (8x8) Styled to match theme (#1E1E1E/101010 background, #2A2D35 thick borders)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF101010))
                    .border(4.dp, Color(0xFF2A2D35), RoundedCornerShape(16.dp))
                    .onGloballyPositioned { boardLayoutCoordinates = it }
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (rowIdx in 0..7) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            for (colIdx in 0..7) {
                                val cell = grid[rowIdx][colIdx]
                                
                                // Determine potential ghost hover shadow preview matching
                                var isHoverPreview = false
                                var hoverColor: Color = Color.Transparent
                                
                                val activeIndexForPreview = if (activeDragIndex != -1) activeDragIndex else selectedIndex
                                val shapeForPreview = choices.getOrNull(activeIndexForPreview)
                                if (shapeForPreview != null && hoveredRowCol != null) {
                                    val startRow = hoveredRowCol!!.first
                                    val startCol = hoveredRowCol!!.second
                                    val relativeRow = rowIdx - startRow
                                    val relativeCol = colIdx - startCol
                                    val isPart = shapeForPreview.getNormalizedCoordinates().any { it.row == relativeRow && it.col == relativeCol }
                                    if (isPart) {
                                        isHoverPreview = true
                                        val canFit = viewModel.canShapeFitAt(shapeForPreview, startRow, startCol)
                                        hoverColor = if (canFit) shapeForPreview.color else Color(0xFFCF6679)
                                    }
                                }

                                BoardCellView(
                                    cellType = cell.type,
                                    cellColor = cell.color,
                                    hasShatterEffect = cell.hasShatterEffect,
                                    isHoverPreview = isHoverPreview,
                                    hoverColor = hoverColor,
                                    onClick = {
                                        viewModel.onCellTapped(rowIdx, colIdx)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                }

                // Visual Indicator overlay if Hammer or Bomb powerups are active!
                if (activePowerUp == PowerUpType.HAMMER) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, Color(0xFFFFD700).copy(alpha = borderAnimAlpha), RoundedCornerShape(20.dp))
                            .background(Color(0x1F000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔨 ВЫБЕРИТЕ БЛОК ДЛЯ СЛОМА",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (activePowerUp == PowerUpType.BOMB) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, Color(0xFFEF4444).copy(alpha = borderAnimAlpha), RoundedCornerShape(20.dp))
                            .background(Color(0x1F000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💣 ВЫБЕРИТЕ ЦЕНТР ДЛЯ ВЗРЫВА 3x3",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Actions: Shop / Power-Up Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle (Cost: 30)
                PowerUpButton(
                    icon = Icons.Default.Autorenew,
                    label = "Шаффл",
                    cost = 30,
                    isActive = false,
                    enabled = stats.totalCoins >= 30,
                    onClick = { viewModel.triggerShuffle() },
                    modifier = Modifier.weight(1f)
                )

                // Rotate (Cost: 20)
                PowerUpButton(
                    icon = Icons.Default.RotateRight,
                    label = "Ротация",
                    cost = 20,
                    isActive = activePowerUp == PowerUpType.ROTATE,
                    enabled = stats.totalCoins >= 20,
                    onClick = { viewModel.activateRotatePowerUp() },
                    modifier = Modifier.weight(1f)
                )

                // Hammer (Cost: 40)
                PowerUpButton(
                    icon = Icons.Default.Gavel,
                    label = "Молоток",
                    cost = 40,
                    isActive = activePowerUp == PowerUpType.HAMMER,
                    enabled = stats.totalCoins >= 40,
                    onClick = { viewModel.toggleHammerPowerUp() },
                    modifier = Modifier.weight(1f)
                )

                // Bomb (Cost: 80)
                PowerUpButton(
                    icon = Icons.Default.Waves,
                    label = "Бомба",
                    cost = 80,
                    isActive = activePowerUp == PowerUpType.BOMB,
                    enabled = stats.totalCoins >= 80,
                    onClick = { viewModel.toggleBombPowerUp() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom active choices (3 shapes selection slots) - Styled with #2A2D35 & neon borders
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x662A2D35)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..2) {
                        val shape = choices[i]
                        val isSelected = selectedIndex == i

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .onGloballyPositioned { shapeSlotCoordinates[i] = it }
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(0x1FBB86FC) else Color(0x12000000)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFBB86FC).copy(alpha = borderAnimAlpha) else Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .pointerInput(i, shape, activePowerUp) {
                                    if (shape == null) return@pointerInput
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            activeDragIndex = i
                                            dragOffset = Offset.Zero
                                            val posInWindow = shapeSlotCoordinates[i]?.localToWindow(Offset.Zero) ?: Offset.Zero
                                            dragPositionOnScreen = posInWindow + offset
                                            viewModel.selectedShapeIndex = i
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount
                                            val posInWindow = shapeSlotCoordinates[i]?.localToWindow(Offset.Zero) ?: Offset.Zero
                                            dragPositionOnScreen = posInWindow + change.position
                                        },
                                        onDragEnd = {
                                            val hovered = hoveredRowCol
                                            if (hovered != null) {
                                                val (r, c) = hovered
                                                if (viewModel.canShapeFitAt(shape, r, c)) {
                                                    viewModel.onCellTapped(r, c)
                                                }
                                            } else {
                                                if (dragOffset.getDistance() < 24f) {
                                                    if (activePowerUp == PowerUpType.ROTATE) {
                                                        viewModel.rotateShapeAtIndex(i)
                                                    } else {
                                                        viewModel.selectedShapeIndex = if (isSelected) -1 else i
                                                    }
                                                }
                                            }
                                            activeDragIndex = -1
                                            dragOffset = Offset.Zero
                                            viewModel.selectedShapeIndex = -1
                                        },
                                        onDragCancel = {
                                            activeDragIndex = -1
                                            dragOffset = Offset.Zero
                                            viewModel.selectedShapeIndex = -1
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (shape != null) {
                                if (activeDragIndex != i) {
                                    BlockShapePreview(shape = shape)
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Empty",
                                    tint = Color(0x22FFFFFF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Overlay Game Over dialog trigger
        if (isGameOver) {
            GameOverOverlayDialog(
                score = currentScore,
                highScore = stats.highScore,
                mode = mode,
                onRestart = {
                    if (mode == GameMode.CLASSIC) viewModel.startClassicGame()
                    else viewModel.startCampaignLevel(levelNum)
                },
                onExit = onBackToMenu
            )
        }

        // Overlay Level Completed dialog trigger
        if (isLevelCompleted) {
            val levelInfo = currentLevelPreset
            LevelCompletedOverlayDialog(
                score = currentScore,
                rewardCoins = levelInfo?.rewardCoins ?: 100,
                starsCount = when {
                    moves == null -> 3
                    moves >= (levelInfo?.maxMoves ?: 30) / 2 -> 3
                    moves >= (levelInfo?.maxMoves ?: 30) / 4 -> 2
                    else -> 1
                },
                onNextLevel = {
                    viewModel.startCampaignLevel(levelNum + 1)
                },
                onExit = onBackToMenu
            )
        }

        // Floating drag shape overlay rendered at the root Box of the screen so it floats on top of all UI components
        if (activeDragIndex != -1) {
            val shape = choices.getOrNull(activeDragIndex)
            val rootCoords = rootLayoutCoordinates
            if (shape != null && rootCoords != null) {
                val localRootOffset = rootCoords.windowToLocal(dragPositionOnScreen)
                val cellSizePx = if (boardLayoutCoordinates != null) {
                    boardLayoutCoordinates!!.size.width / 8f
                } else {
                    with(density) { 40.dp.toPx() }
                }
                val cellSizeDp = with(density) { cellSizePx.toDp() }
                
                // Lift shape upwards slightly when dragged so finger doesn't block blocks!
                val liftOffsetPx = with(density) { -55.dp.toPx() }
                
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (localRootOffset.x - (shape.width * cellSizePx) / 2f).roundToInt(),
                                y = (localRootOffset.y - (shape.height * cellSizePx) / 2f + liftOffsetPx).roundToInt()
                            )
                        }
                ) {
                    DraggingShapeView(shape = shape, cellSizeDp = cellSizeDp)
                }
            }
        }
    }
}

@Composable
fun PowerUpButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    cost: Int,
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isActive -> Color(0xFF03DAC6)
                    enabled -> Color(0xFF2A2D35)
                    else -> Color(0xFF1E1E1E)
                }
            )
            .border(
                1.dp,
                when {
                    isActive -> Color(0xFF03DAC6)
                    enabled -> Color.White.copy(alpha = 0.12f)
                    else -> Color.White.copy(alpha = 0.04f)
                },
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled || isActive, onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = when {
                isActive -> Color(0xFF121212)
                enabled -> Color.White
                else -> Color(0xFFB3B3B3).copy(alpha = 0.4f)
            },
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            color = when {
                isActive -> Color(0xFF121212)
                enabled -> Color.White
                else -> Color(0xFFB3B3B3).copy(alpha = 0.4f)
            }
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = if (isActive) Color(0xFF121212) else Color(0xFF03DAC6),
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "$cost",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = when {
                    isActive -> Color(0xFF121212)
                    enabled -> Color.White
                    else -> Color(0xFFB3B3B3).copy(alpha = 0.4f)
                }
            )
        }
    }
}

@Composable
fun BoardCellView(
    cellType: CellType,
    cellColor: Color,
    hasShatterEffect: Boolean,
    isHoverPreview: Boolean,
    hoverColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Blast/shatter animation variables
    val scaleAnim by animateFloatAsState(
        targetValue = if (hasShatterEffect) 0f else 1f,
        animationSpec = tween(300), label = ""
    )

    // Smooth progress for particles (from 0 to 1 when hasShatterEffect is true)
    val shatterProgress by animateFloatAsState(
        targetValue = if (hasShatterEffect) 1f else 0f,
        animationSpec = tween(300, easing = LinearEasing),
        label = "shatter"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Core Cell Background & Content (Clipped to cell shape)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when (cellType) {
                        CellType.EMPTY -> {
                            if (isHoverPreview) hoverColor.copy(alpha = 0.45f)
                            else Color.White.copy(alpha = 0.05f) // Faint translucent dark socket slot
                        }
                        CellType.FILLED -> cellColor
                        CellType.OBSTACLE -> Color(0xFF475569) // Slated dark gray stone
                        CellType.FROZEN -> Color(0xFF03DAC6)  // Teal frozen base
                    }
                )
                .then(
                    if (cellType != CellType.EMPTY) {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(6.dp)
                        )
                    } else Modifier
                )
        ) {
            // Render detailed visual textures on filled blocks
            if (cellType != CellType.EMPTY) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bevelSize = 4.dp.toPx()
                    // Top Light Bevel Highlight
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = bevelSize,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = bevelSize,
                        cap = StrokeCap.Round
                    )
                    // Bottom Dark Shadow Bevel
                    drawLine(
                        color = Color.Black.copy(alpha = 0.25f),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = bevelSize,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.25f),
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = bevelSize,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw ICE frozen translucent overlay
            if (cellType == CellType.FROZEN) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color(0xFFE0F7FA))
                        .background(Color(0x9900E5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    // Frost/Snow symbol icon
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Frozen",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
                }
            }

            // Draw STONE obstacle cracks
            if (cellType == CellType.OBSTACLE) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw cool crack lines matching real physical obstacles
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2f
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.1f),
                        size = size
                    )
                }
            }
        }

        // Particle Shatter Overlay - Rendered OUTSIDE of core cell's clipped bounds!
        if (shatterProgress > 0f && shatterProgress < 1f && cellColor != Color.Transparent) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val numParticles = 10
                val parentWidth = size.width
                val parentHeight = size.height
                val center = Offset(parentWidth / 2f, parentHeight / 2f)

                for (i in 0 until numParticles) {
                    // Spread angles uniformly with a bit of unique wobble per particle index
                    val angleOffset = kotlin.math.sin(i * 12.3f) * 0.3f
                    val baseAngle = (i * (2f * Math.PI.toFloat() / numParticles)) + angleOffset
                    
                    // Different speeds for particles to create depth
                    val speedFactor = 0.4f + 0.9f * kotlin.math.abs(kotlin.math.cos(i * 7.9f))
                    val maxDistance = parentWidth * 2.2f // Expand widely outside the cell size bounds!
                    val currentDistance = maxDistance * shatterProgress * speedFactor

                    // Calculate position of particle center
                    val px = center.x + kotlin.math.cos(baseAngle) * currentDistance
                    // Gravity pulling them down as progress increases
                    val gravityOffset = parentHeight * 1.5f * (shatterProgress * shatterProgress)
                    val py = center.y + kotlin.math.sin(baseAngle) * currentDistance + gravityOffset

                    // Size of particle starts large and shrinks to 0
                    val originalSize = parentWidth * 0.28f
                    val particleSize = originalSize * (1f - shatterProgress)
                    val alpha = 1f - shatterProgress

                    if (particleSize > 0f) {
                        // Alternate drawing rotating circles and square shards
                        if (i % 2 == 0) {
                            drawCircle(
                                color = cellColor.copy(alpha = alpha),
                                radius = particleSize / 2f,
                                center = Offset(px, py)
                            )
                        } else {
                            val halfSz = particleSize / 2f
                            drawRect(
                                color = cellColor.copy(alpha = alpha),
                                topLeft = Offset(px - halfSz, py - halfSz),
                                size = Size(particleSize, particleSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 2D Scaled Matrix Preview for the active shape choices in slots
@Composable
fun BlockShapePreview(shape: BlockShape) {
    val normCoords = shape.getNormalizedCoordinates()
    val columns = shape.width
    val rows = shape.height

    Box(
        modifier = Modifier
            .padding(6.dp)
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (r in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (c in 0 until columns) {
                        val isFilled = normCoords.contains(com.example.blockblast.model.Coordinate(r, c))
                        
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isFilled) shape.color else Color.Transparent
                                )
                                .border(
                                    width = if (isFilled) 0.5.dp else 0.dp,
                                    color = if (isFilled) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                                    shape = RoundedCornerShape(3.dp)
                                )
                        ) {
                            // Extra mini glossy overlay for realistic blocks
                            if (isFilled) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawRect(
                                        color = Color.White.copy(alpha = 0.15f),
                                        size = Size(size.width, size.height / 2f)
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

@Composable
fun GameOverOverlayDialog(
    score: Int,
    highScore: Int,
    mode: GameMode,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            border = BorderStroke(4.dp, Color(0xFFCF6679))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SentimentVeryDissatisfied,
                    contentDescription = "Dissatisfied",
                    tint = Color(0xFFCF6679),
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ИГРА ОКОНЧЕНА!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = if (mode == GameMode.CAMPAIGN) "Лимит ходов исчерпан или блоки больше некуда разместить." 
                           else "Больше нет доступных ячеек для размещения блоков.",
                    fontSize = 12.sp,
                    color = Color(0xFFB3B3B3),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "РЕЗУЛЬТАТ", fontSize = 10.sp, color = Color(0xFFB3B3B3), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Text(text = "$score", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    
                    if (mode == GameMode.CLASSIC) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "РЕКОРД", fontSize = 10.sp, color = Color(0xFFB3B3B3), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(text = "$highScore", fontSize = 26.sp, color = Color(0xFFBB86FC), fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "ПОПРОБОВАТЬ СНОВА", fontWeight = FontWeight.Black, color = Color(0xFF121212), letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onExit) {
                    Text(text = "ВЫЙТИ В МЕНЮ", color = Color(0xFFB3B3B3), fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun LevelCompletedOverlayDialog(
    score: Int,
    rewardCoins: Int,
    starsCount: Int,
    onNextLevel: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            border = BorderStroke(4.dp, Color(0xFF03DAC6)) // Neon Teal Triumph Styling
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = "Victory",
                    tint = Color(0xFF03DAC6),
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "УРОВЕНЬ ЗАВЕРШЕН!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars rating path
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    for (i in 1..3) {
                        val isWinStar = i <= starsCount
                        val starScale by animateFloatAsState(
                            targetValue = if (isWinStar) 1.2f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = ""
                        )
                        Icon(
                            imageVector = if (isWinStar) Icons.Filled.Star else Icons.Default.Star,
                            contentDescription = "completed star",
                            tint = if (isWinStar) Color(0xFFFFB703) else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .size(40.dp)
                                .scale(starScale)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row styled
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A2D35))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ОЧКИ", fontSize = 10.sp, color = Color(0xFFB3B3B3), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Text(text = "$score", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "НАГРАДА", fontSize = 10.sp, color = Color(0xFFB3B3B3), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "", tint = Color(0xFF03DAC6), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "+$rewardCoins", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "СЛЕДУЮЩИЙ УРОВЕНЬ", fontWeight = FontWeight.Black, color = Color(0xFF121212), letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onExit) {
                    Text(text = "ВЫЙТИ В КАРТУ", color = Color(0xFFB3B3B3), fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun DraggingShapeView(shape: BlockShape, cellSizeDp: androidx.compose.ui.unit.Dp) {
    val normCoords = shape.getNormalizedCoordinates()
    val columns = shape.width
    val rows = shape.height

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (r in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (c in 0 until columns) {
                    val isFilled = normCoords.contains(com.example.blockblast.model.Coordinate(r, c))
                    Box(
                        modifier = Modifier
                            .size(cellSizeDp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isFilled) shape.color else Color.Transparent
                            )
                            .border(
                                width = if (isFilled) 1.dp else 0.dp,
                                color = if (isFilled) Color.White.copy(alpha = 0.25f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }
    }
}
