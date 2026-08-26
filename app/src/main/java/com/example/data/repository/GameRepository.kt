package com.example.data.repository

import com.example.data.local.dao.GameDao
import com.example.data.local.dao.RoundDao
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.engine.ScoreEngine
import com.example.domain.model.GameMode
import com.example.domain.model.RoundInput
import com.example.domain.model.RoundScoreResult
import com.example.domain.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(
    private val gameDao: GameDao,
    private val roundDao: RoundDao
) {
    fun getActiveGameFlow(): Flow<GameEntity?> = gameDao.getActiveGameFlow()

    fun getAllGamesFlow(): Flow<List<GameEntity>> = gameDao.getAllGames()

    fun getGameByIdFlow(gameId: Long): Flow<GameEntity?> = gameDao.getGameByIdFlow(gameId)

    fun getRoundsForGameFlow(gameId: Long): Flow<List<RoundEntity>> = roundDao.getRoundsForGameFlow(gameId)

    suspend fun startNewGame(
        team1Name: String,
        team2Name: String,
        gameMode: GameMode,
        targetScore: Int,
        yasaEnabled: Boolean
    ): Long = withContext(Dispatchers.IO) {
        val t1 = team1Name.trim().ifEmpty { Team.TEAM_1.defaultPersianName }
        val t2 = team2Name.trim().ifEmpty { Team.TEAM_2.defaultPersianName }

        val game = GameEntity(
            team1Name = t1,
            team2Name = t2,
            gameMode = gameMode.name,
            targetScore = targetScore,
            yasaEnabled = yasaEnabled,
            isFinished = false,
            winnerTeam = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        gameDao.insertGame(game)
    }

    suspend fun addRound(
        gameId: Long,
        input: RoundInput,
        gameMode: GameMode,
        yasaEnabled: Boolean,
        targetScore: Int
    ): RoundScoreResult = withContext(Dispatchers.IO) {
        val existingRounds = roundDao.getRoundsForGame(gameId)
        val nextRoundNumber = existingRounds.size + 1

        val calculation = ScoreEngine.calculateRound(input, gameMode, yasaEnabled)

        val entity = RoundEntity(
            gameId = gameId,
            roundNumber = nextRoundNumber,
            hakimTeam = input.hakimTeam.name,
            bid = calculation.bid,
            hakimEarnedPoints = calculation.hakimEarnedPoints,
            team1ScoreDelta = calculation.team1ScoreDelta,
            team2ScoreDelta = calculation.team2ScoreDelta,
            status = calculation.status.name,
            isYasa = calculation.isYasa,
            isShelem = calculation.isShelem,
            isNegativeShelem = calculation.isNegativeShelem,
            createdAt = System.currentTimeMillis()
        )
        roundDao.insertRound(entity)

        // Check if target score is reached and update game status
        val allRounds = roundDao.getRoundsForGame(gameId)
        recalculateAndSaveGameStatus(gameId, allRounds, targetScore)

        calculation
    }

    suspend fun updateRound(
        roundId: Long,
        gameId: Long,
        input: RoundInput,
        gameMode: GameMode,
        yasaEnabled: Boolean,
        targetScore: Int
    ): RoundScoreResult = withContext(Dispatchers.IO) {
        val existingRounds = roundDao.getRoundsForGame(gameId)
        val targetRound = existingRounds.firstOrNull { it.id == roundId }
            ?: throw IllegalArgumentException("Round not found")

        val calculation = ScoreEngine.calculateRound(input, gameMode, yasaEnabled)

        val updatedEntity = targetRound.copy(
            hakimTeam = input.hakimTeam.name,
            bid = calculation.bid,
            hakimEarnedPoints = calculation.hakimEarnedPoints,
            team1ScoreDelta = calculation.team1ScoreDelta,
            team2ScoreDelta = calculation.team2ScoreDelta,
            status = calculation.status.name,
            isYasa = calculation.isYasa,
            isShelem = calculation.isShelem,
            isNegativeShelem = calculation.isNegativeShelem
        )
        roundDao.updateRound(updatedEntity)

        val allRounds = roundDao.getRoundsForGame(gameId)
        recalculateAndSaveGameStatus(gameId, allRounds, targetScore)

        calculation
    }

    suspend fun deleteRound(
        roundId: Long,
        gameId: Long,
        targetScore: Int
    ) = withContext(Dispatchers.IO) {
        roundDao.deleteRoundById(roundId)
        // Renumber remaining rounds sequentially
        val remaining = roundDao.getRoundsForGame(gameId)
        remaining.forEachIndexed { index, round ->
            val expectedRoundNum = index + 1
            if (round.roundNumber != expectedRoundNum) {
                roundDao.updateRound(round.copy(roundNumber = expectedRoundNum))
            }
        }
        val updatedList = roundDao.getRoundsForGame(gameId)
        recalculateAndSaveGameStatus(gameId, updatedList, targetScore)
    }

    suspend fun undoLastRound(
        gameId: Long,
        targetScore: Int
    ) = withContext(Dispatchers.IO) {
        roundDao.deleteLastRound(gameId)
        val allRounds = roundDao.getRoundsForGame(gameId)
        recalculateAndSaveGameStatus(gameId, allRounds, targetScore)
    }

    suspend fun finishGameManually(gameId: Long, winnerTeam: Team?) = withContext(Dispatchers.IO) {
        val game = gameDao.getGameById(gameId) ?: return@withContext
        gameDao.updateGame(
            game.copy(
                isFinished = true,
                winnerTeam = winnerTeam?.name,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun reopenGame(gameId: Long) = withContext(Dispatchers.IO) {
        val game = gameDao.getGameById(gameId) ?: return@withContext
        gameDao.updateGame(
            game.copy(
                isFinished = false,
                winnerTeam = null,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteGame(gameId: Long) = withContext(Dispatchers.IO) {
        roundDao.deleteRoundsForGame(gameId)
        gameDao.deleteGameById(gameId)
    }

    private suspend fun recalculateAndSaveGameStatus(
        gameId: Long,
        rounds: List<RoundEntity>,
        targetScore: Int
    ) {
        val game = gameDao.getGameById(gameId) ?: return
        val results = rounds.map { round ->
            val hTeam = if (round.hakimTeam == Team.TEAM_2.name) Team.TEAM_2 else Team.TEAM_1
            val gMode = GameMode.fromString(game.gameMode)
            val oppEarned = gMode.totalPoints - round.hakimEarnedPoints
            RoundScoreResult(
                hakimTeam = hTeam,
                bid = round.bid,
                hakimEarnedPoints = round.hakimEarnedPoints,
                opponentEarnedPoints = oppEarned,
                team1ScoreDelta = round.team1ScoreDelta,
                team2ScoreDelta = round.team2ScoreDelta,
                status = com.example.domain.model.RoundContractStatus.valueOf(round.status),
                isYasa = round.isYasa,
                isShelem = round.isShelem,
                isNegativeShelem = round.isNegativeShelem,
                explanationPersian = ""
            )
        }

        val summary = ScoreEngine.summarizeGame(results, targetScore)
        val updatedGame = game.copy(
            isFinished = summary.isGameOver,
            winnerTeam = summary.winnerTeam?.name,
            updatedAt = System.currentTimeMillis()
        )
        gameDao.updateGame(updatedGame)
    }
}
