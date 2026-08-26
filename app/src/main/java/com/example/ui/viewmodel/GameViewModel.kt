package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.data.repository.GameRepository
import com.example.data.repository.SettingsRepository
import com.example.domain.engine.ScoreEngine
import com.example.domain.model.GameMode
import com.example.domain.model.GameScoreSummary
import com.example.domain.model.GameSettings
import com.example.domain.model.RoundContractStatus
import com.example.domain.model.RoundInput
import com.example.domain.model.RoundScoreResult
import com.example.domain.model.Team
import com.example.util.SoundAndHapticHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveGameState(
    val game: GameEntity? = null,
    val rounds: List<RoundEntity> = emptyList(),
    val summary: GameScoreSummary = GameScoreSummary(0, 0, 1, null, 0, false, null),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = GameRepository(database.gameDao(), database.roundDao())
    val settingsRepository = SettingsRepository(application)

    val settings: StateFlow<GameSettings> = settingsRepository.settingsFlow

    // Current active game flow
    private val activeGameFlow = repository.getActiveGameFlow()

    val activeGameState: StateFlow<ActiveGameState> = activeGameFlow.flatMapLatest { game ->
        if (game == null) {
            flowOf(ActiveGameState(game = null, rounds = emptyList(), isLoading = false))
        } else {
            repository.getRoundsForGameFlow(game.id).mapRoundsToState(game)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActiveGameState(isLoading = true)
    )

    val allGames: StateFlow<List<GameEntity>> = repository.getAllGamesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI Dialog & Sheet states
    private val _isNewGameDialogOpen = MutableStateFlow(false)
    val isNewGameDialogOpen: StateFlow<Boolean> = _isNewGameDialogOpen.asStateFlow()

    private val _isAddEditRoundDialogOpen = MutableStateFlow(false)
    val isAddEditRoundDialogOpen: StateFlow<Boolean> = _isAddEditRoundDialogOpen.asStateFlow()

    private val _editingRound = MutableStateFlow<RoundEntity?>(null)
    val editingRound: StateFlow<RoundEntity?> = _editingRound.asStateFlow()

    private val _showWinnerCelebration = MutableStateFlow(false)
    val showWinnerCelebration: StateFlow<Boolean> = _showWinnerCelebration.asStateFlow()

    private val _selectedGameForHistoryDetail = MutableStateFlow<GameEntity?>(null)
    val selectedGameForHistoryDetail: StateFlow<GameEntity?> = _selectedGameForHistoryDetail.asStateFlow()

    fun openNewGameDialog() {
        _isNewGameDialogOpen.value = true
    }

    fun closeNewGameDialog() {
        _isNewGameDialogOpen.value = false
    }

    fun openAddRoundDialog() {
        _editingRound.value = null
        _isAddEditRoundDialogOpen.value = true
    }

    fun openEditRoundDialog(round: RoundEntity) {
        _editingRound.value = round
        _isAddEditRoundDialogOpen.value = true
    }

    fun closeAddEditRoundDialog() {
        _isAddEditRoundDialogOpen.value = false
        _editingRound.value = null
    }

    fun dismissWinnerCelebration() {
        _showWinnerCelebration.value = false
    }

    fun selectHistoryGame(game: GameEntity?) {
        _selectedGameForHistoryDetail.value = game
    }

    fun startNewGame(
        team1Name: String,
        team2Name: String,
        gameMode: GameMode,
        targetScore: Int? = null,
        yasaEnabled: Boolean? = null
    ) {
        viewModelScope.launch {
            val appSettings = settings.value
            val target = targetScore ?: when (gameMode) {
                GameMode.WITHOUT_JOKER -> appSettings.targetScoreWithoutJoker
                GameMode.WITH_JOKER -> appSettings.targetScoreWithJoker
            }
            val yasa = yasaEnabled ?: appSettings.yasaEnabled

            repository.startNewGame(
                team1Name = team1Name,
                team2Name = team2Name,
                gameMode = gameMode,
                targetScore = target,
                yasaEnabled = yasa
            )
            _isNewGameDialogOpen.value = false
            SoundAndHapticHelper.playSuccessSound(getApplication(), appSettings.soundEffectsEnabled)
            SoundAndHapticHelper.triggerVibration(getApplication())
        }
    }

    fun submitRound(input: RoundInput) {
        val currentState = activeGameState.value
        val game = currentState.game ?: return
        val gameMode = GameMode.fromString(game.gameMode)
        val appSettings = settings.value

        viewModelScope.launch {
            val editing = _editingRound.value
            val result = if (editing != null) {
                repository.updateRound(
                    roundId = editing.id,
                    gameId = game.id,
                    input = input,
                    gameMode = gameMode,
                    yasaEnabled = game.yasaEnabled,
                    targetScore = game.targetScore
                )
            } else {
                repository.addRound(
                    gameId = game.id,
                    input = input,
                    gameMode = gameMode,
                    yasaEnabled = game.yasaEnabled,
                    targetScore = game.targetScore
                )
            }

            closeAddEditRoundDialog()

            // Play sound effect based on result
            if (result.isShelem) {
                SoundAndHapticHelper.playShelemFanfare(getApplication(), appSettings.soundEffectsEnabled)
                SoundAndHapticHelper.triggerVibration(getApplication(), strong = true)
            } else if (result.isYasa) {
                SoundAndHapticHelper.playAlertSound(getApplication(), appSettings.soundEffectsEnabled)
                SoundAndHapticHelper.triggerVibration(getApplication(), strong = true)
            } else {
                SoundAndHapticHelper.playSuccessSound(getApplication(), appSettings.soundEffectsEnabled)
                SoundAndHapticHelper.triggerVibration(getApplication())
            }
        }
    }

    fun deleteRound(roundId: Long) {
        val game = activeGameState.value.game ?: return
        viewModelScope.launch {
            repository.deleteRound(roundId, game.id, game.targetScore)
            SoundAndHapticHelper.playClickSound(getApplication(), settings.value.soundEffectsEnabled)
        }
    }

    fun undoLastRound() {
        val game = activeGameState.value.game ?: return
        viewModelScope.launch {
            repository.undoLastRound(game.id, game.targetScore)
            SoundAndHapticHelper.playClickSound(getApplication(), settings.value.soundEffectsEnabled)
            SoundAndHapticHelper.triggerVibration(getApplication())
        }
    }

    fun resumeGame(gameId: Long) {
        viewModelScope.launch {
            repository.reopenGame(gameId)
            _selectedGameForHistoryDetail.value = null
            SoundAndHapticHelper.playClickSound(getApplication(), settings.value.soundEffectsEnabled)
        }
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            repository.deleteGame(gameId)
            if (_selectedGameForHistoryDetail.value?.id == gameId) {
                _selectedGameForHistoryDetail.value = null
            }
            SoundAndHapticHelper.playClickSound(getApplication(), settings.value.soundEffectsEnabled)
        }
    }
}

// Extension to map Flow of rounds to ActiveGameState
private fun kotlinx.coroutines.flow.Flow<List<RoundEntity>>.mapRoundsToState(game: GameEntity): kotlinx.coroutines.flow.Flow<ActiveGameState> {
    return this.map { rounds: List<RoundEntity> ->
        val gMode = GameMode.fromString(game.gameMode)
        val roundResults: List<RoundScoreResult> = rounds.map { round: RoundEntity ->
            val hTeam = if (round.hakimTeam == Team.TEAM_2.name) Team.TEAM_2 else Team.TEAM_1
            val oppPoints = gMode.totalPoints - round.hakimEarnedPoints
            RoundScoreResult(
                hakimTeam = hTeam,
                bid = round.bid,
                hakimEarnedPoints = round.hakimEarnedPoints,
                opponentEarnedPoints = oppPoints,
                team1ScoreDelta = round.team1ScoreDelta,
                team2ScoreDelta = round.team2ScoreDelta,
                status = try {
                    RoundContractStatus.valueOf(round.status)
                } catch (_: Exception) {
                    RoundContractStatus.SUCCESS
                },
                isYasa = round.isYasa,
                isShelem = round.isShelem,
                isNegativeShelem = round.isNegativeShelem,
                explanationPersian = ""
            )
        }

        val summary = ScoreEngine.summarizeGame(roundResults, game.targetScore)
        ActiveGameState(
            game = game,
            rounds = rounds,
            summary = summary,
            isLoading = false
        )
    }
}
