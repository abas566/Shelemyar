package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TableRestaurant
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.GameMode
import com.example.ui.components.AddEditRoundDialog
import com.example.ui.components.NewGameDialog
import com.example.ui.components.WinnerCelebrationDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ShelemyarTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.SettingsViewModel

enum class NavigationTab(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    SCOREBOARD("میز بازی", Icons.Filled.TableRestaurant, Icons.Outlined.TableRestaurant),
    HISTORY("تاریخچه", Icons.Filled.History, Icons.Outlined.History),
    RULES("قوانین", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    SETTINGS("تنظیمات", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel
) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val activeState by gameViewModel.activeGameState.collectAsStateWithLifecycle()
    val allGames by gameViewModel.allGames.collectAsStateWithLifecycle()

    val isNewGameDialogOpen by gameViewModel.isNewGameDialogOpen.collectAsStateWithLifecycle()
    val isAddEditRoundDialogOpen by gameViewModel.isAddEditRoundDialogOpen.collectAsStateWithLifecycle()
    val editingRound by gameViewModel.editingRound.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(NavigationTab.SCOREBOARD) }
    var hasShownWinnerDialogForGameId by remember { mutableStateOf<Long?>(null) }
    var showCelebrationDialog by remember { mutableStateOf(false) }

    // Auto-trigger winner celebration when a game finishes
    LaunchedEffect(activeState.summary.isGameOver, activeState.game?.id) {
        val gameId = activeState.game?.id
        if (activeState.summary.isGameOver && gameId != null && hasShownWinnerDialogForGameId != gameId) {
            hasShownWinnerDialogForGameId = gameId
            showCelebrationDialog = true
        }
    }

    // Determine dark theme
    val isDark = when (settings.isDarkMode) {
        true -> true
        false -> false
        null -> isSystemInDarkTheme()
    }

    ShelemyarTheme(darkTheme = isDark) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "♠️",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "شلمیار",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "♥️",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { gameViewModel.openNewGameDialog() },
                            modifier = Modifier.testTag("appbar_new_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "بازی جدید",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    NavigationTab.SCOREBOARD -> {
                        ScoreboardScreen(
                            game = activeState.game,
                            rounds = activeState.rounds,
                            summary = activeState.summary,
                            onNewGameClick = { gameViewModel.openNewGameDialog() },
                            onAddRoundClick = { gameViewModel.openAddRoundDialog() },
                            onEditRoundClick = { round -> gameViewModel.openEditRoundDialog(round) },
                            onDeleteRoundClick = { id -> gameViewModel.deleteRound(id) },
                            onUndoLastRoundClick = { gameViewModel.undoLastRound() }
                        )
                    }
                    NavigationTab.HISTORY -> {
                        HistoryScreen(
                            games = allGames,
                            onResumeGame = { gameId ->
                                gameViewModel.resumeGame(gameId)
                                selectedTab = NavigationTab.SCOREBOARD
                            },
                            onDeleteGame = { gameId -> gameViewModel.deleteGame(gameId) }
                        )
                    }
                    NavigationTab.RULES -> {
                        RulesScreen()
                    }
                    NavigationTab.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            onUpdateYasa = { settingsViewModel.setYasaEnabled(it) },
                            onUpdateDarkMode = { settingsViewModel.setDarkMode(it) },
                            onUpdateAnimations = { settingsViewModel.setAnimationsEnabled(it) },
                            onUpdateSound = { settingsViewModel.setSoundEffectsEnabled(it) },
                            onUpdateTargetScores = { without, with -> settingsViewModel.setTargetScores(without, with) },
                            onResetDefaults = { settingsViewModel.resetDefaults() }
                        )
                    }
                }
            }
        }

        // Dialogs
        if (isNewGameDialogOpen) {
            NewGameDialog(
                currentSettings = settings,
                onDismiss = { gameViewModel.closeNewGameDialog() },
                onStartGame = { team1, team2, mode, target, yasa ->
                    gameViewModel.startNewGame(team1, team2, mode, target, yasa)
                    selectedTab = NavigationTab.SCOREBOARD
                }
            )
        }

        if (isAddEditRoundDialogOpen && activeState.game != null) {
            AddEditRoundDialog(
                game = activeState.game!!,
                existingRound = editingRound,
                onDismiss = { gameViewModel.closeAddEditRoundDialog() },
                onSubmit = { input -> gameViewModel.submitRound(input) }
            )
        }

        if (showCelebrationDialog && activeState.game != null && activeState.summary.isGameOver) {
            WinnerCelebrationDialog(
                game = activeState.game!!,
                summary = activeState.summary,
                rounds = activeState.rounds,
                onDismiss = { showCelebrationDialog = false },
                onNewGame = {
                    showCelebrationDialog = false
                    gameViewModel.openNewGameDialog()
                }
            )
        }
    }
}
