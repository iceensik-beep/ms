package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.blockblast.data.GameDatabase
import com.example.blockblast.data.GameRepository
import com.example.blockblast.ui.GameScreen
import com.example.blockblast.ui.MainMenuScreen
import com.example.blockblast.viewmodel.GameViewModel
import com.example.blockblast.viewmodel.GameViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Local SQLite Persistence via Room
        val database = GameDatabase.getDatabase(applicationContext)
        val repository = GameRepository(database.gameDao())

        // Initialise state ViewModel with Factory
        val viewModelId: GameViewModel by viewModels {
            GameViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Simple custom local state router
                    var currentScreen by remember { mutableStateOf("menu") }

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            "menu" -> {
                                MainMenuScreen(
                                    viewModel = viewModelId,
                                    onStartClassic = {
                                        viewModelId.startClassicGame()
                                        currentScreen = "game"
                                    },
                                    onStartCampaign = { levelNum ->
                                        viewModelId.startCampaignLevel(levelNum)
                                        currentScreen = "game"
                                    }
                                )
                            }
                            "game" -> {
                                GameScreen(
                                    viewModel = viewModelId,
                                    onBackToMenu = {
                                        currentScreen = "menu"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
