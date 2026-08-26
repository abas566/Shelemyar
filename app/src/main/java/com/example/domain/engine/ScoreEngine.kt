package com.example.domain.engine

import com.example.domain.model.GameMode
import com.example.domain.model.GameScoreSummary
import com.example.domain.model.RoundContractStatus
import com.example.domain.model.RoundInput
import com.example.domain.model.RoundScoreResult
import com.example.domain.model.Team
import kotlin.math.abs

/**
 * ScoreEngine handles all deterministic calculations and validation for Persian Shelem.
 * Completely offline, pure, and thoroughly tested.
 */
object ScoreEngine {

    const val MINIMUM_BID = 100
    const val BID_STEP = 5
    const val SCORE_STEP = 5

    const val JOKER_RED_VALUE = 20
    const val JOKER_BLACK_VALUE = 15

    fun getTotalPoints(gameMode: GameMode): Int = gameMode.totalPoints

    fun getValidBids(gameMode: GameMode): List<Int> {
        val max = getTotalPoints(gameMode)
        return (MINIMUM_BID..max step BID_STEP).toList()
    }

    fun getValidScoreOptions(gameMode: GameMode): List<Int> {
        val max = getTotalPoints(gameMode)
        return (0..max step SCORE_STEP).toList()
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val errorMessagePersian: String) : ValidationResult()
    }

    fun validateInput(input: RoundInput, gameMode: GameMode): ValidationResult {
        val totalPoints = getTotalPoints(gameMode)

        if (input.bid < MINIMUM_BID) {
            return ValidationResult.Invalid("حداقل مقدار خوانده ۱۰۰ است.")
        }
        if (input.bid > totalPoints) {
            return ValidationResult.Invalid("مقدار خوانده نمی‌تواند بیشتر از مجموع امتیازات ($totalPoints) باشد.")
        }
        if (input.bid % BID_STEP != 0) {
            return ValidationResult.Invalid("مقدار خوانده باید مضربی از ۵ باشد.")
        }
        if (input.hakimEarnedPoints < 0) {
            return ValidationResult.Invalid("امتیاز کسب‌شده نمی‌تواند منفی باشد.")
        }
        if (input.hakimEarnedPoints > totalPoints) {
            return ValidationResult.Invalid("امتیاز کسب‌شده نمی‌تواند بیشتر از $totalPoints باشد.")
        }
        if (input.hakimEarnedPoints % SCORE_STEP != 0) {
            return ValidationResult.Invalid("امتیاز کسب‌شده باید مضربی از ۵ باشد.")
        }

        return ValidationResult.Valid
    }

    /**
     * Calculates the score deltas for a single round according to the official Shelem rules.
     */
    fun calculateRound(
        input: RoundInput,
        gameMode: GameMode,
        yasaEnabled: Boolean = true
    ): RoundScoreResult {
        val totalPoints = getTotalPoints(gameMode)
        val hakimEarned = input.hakimEarnedPoints.coerceIn(0, totalPoints)
        val opponentEarned = totalPoints - hakimEarned
        val bid = input.bid

        val isShelem = (hakimEarned == totalPoints) || input.isShelemDeclared && hakimEarned >= bid
        val isNegativeShelem = (hakimEarned == 0 && opponentEarned == totalPoints)
        val isSuccess = !isNegativeShelem && (hakimEarned >= bid)

        val hakimScoreDelta: Int
        val opponentScoreDelta: Int
        val status: RoundContractStatus
        val isYasa: Boolean
        val explanation: String

        when {
            // Case 1: Negative Shelem (Opponent got all points!)
            isNegativeShelem -> {
                status = RoundContractStatus.NEGATIVE_SHELEM
                isYasa = yasaEnabled
                hakimScoreDelta = if (yasaEnabled) -2 * bid else -1 * bid
                opponentScoreDelta = 2 * totalPoints // Opponent gets doubled shelem
                explanation = "شلم منفی! تیم حاکم هیچ امتیازی نگرفت. رقیب ${opponentScoreDelta}+ و حاکم ${hakimScoreDelta} شد."
            }

            // Case 2: Shelem (Hakim team got all points or fulfilled declared Shelem)
            isShelem -> {
                status = RoundContractStatus.SHELEM
                isYasa = false
                hakimScoreDelta = 2 * bid // Doubled contract points
                opponentScoreDelta = 0
                explanation = "شلم! امتیاز قرارداد دو برابر شد (${hakimScoreDelta}+) برای تیم حاکم."
            }

            // Case 3: Normal Success (Hakim reached/exceeded bid)
            isSuccess -> {
                status = RoundContractStatus.SUCCESS
                isYasa = false
                hakimScoreDelta = hakimEarned
                opponentScoreDelta = opponentEarned
                explanation = "قرارداد موفق! تیم حاکم ${hakimScoreDelta}+ و رقیب ${opponentScoreDelta}+ امتیاز گرفتند."
            }

            // Case 4: Fall / Yasa (Hakim failed to make the contract)
            else -> {
                status = RoundContractStatus.FALL_YASA
                isYasa = yasaEnabled
                hakimScoreDelta = if (yasaEnabled) -2 * bid else -1 * bid
                opponentScoreDelta = opponentEarned
                explanation = if (yasaEnabled) {
                    "یاسا! تیم حاکم افتاد؛ جریمه منفی دو برابر شد (${hakimScoreDelta}) و رقیب ${opponentScoreDelta}+ گرفت."
                } else {
                    "افتادن قرارداد! تیم حاکم ${hakimScoreDelta} شد و رقیb ${opponentScoreDelta}+ گرفت."
                }
            }
        }

        val team1Delta = if (input.hakimTeam == Team.TEAM_1) hakimScoreDelta else opponentScoreDelta
        val team2Delta = if (input.hakimTeam == Team.TEAM_2) hakimScoreDelta else opponentScoreDelta

        return RoundScoreResult(
            hakimTeam = input.hakimTeam,
            bid = bid,
            hakimEarnedPoints = hakimEarned,
            opponentEarnedPoints = opponentEarned,
            team1ScoreDelta = team1Delta,
            team2ScoreDelta = team2Delta,
            status = status,
            isYasa = isYasa && !isSuccess && !isShelem,
            isShelem = isShelem,
            isNegativeShelem = isNegativeShelem,
            explanationPersian = explanation
        )
    }

    /**
     * Aggregates cumulative round scores into a comprehensive game summary.
     */
    fun summarizeGame(
        roundResults: List<RoundScoreResult>,
        targetScore: Int
    ): GameScoreSummary {
        var team1Total = 0
        var team2Total = 0

        roundResults.forEach { result ->
            team1Total += result.team1ScoreDelta
            team2Total += result.team2ScoreDelta
        }

        val roundCount = roundResults.size
        val scoreDiff = abs(team1Total - team2Total)

        val leadingTeam: Team? = when {
            team1Total > team2Total -> Team.TEAM_1
            team2Total > team1Total -> Team.TEAM_2
            else -> null
        }

        val isTeam1TargetReached = team1Total >= targetScore
        val isTeam2TargetReached = team2Total >= targetScore

        val isGameOver: Boolean
        val winnerTeam: Team?

        if (isTeam1TargetReached && isTeam2TargetReached) {
            // Both crossed target in the same round, higher score wins, or if tied, continues
            if (team1Total > team2Total) {
                isGameOver = true
                winnerTeam = Team.TEAM_1
            } else if (team2Total > team1Total) {
                isGameOver = true
                winnerTeam = Team.TEAM_2
            } else {
                isGameOver = false
                winnerTeam = null
            }
        } else if (isTeam1TargetReached) {
            isGameOver = true
            winnerTeam = Team.TEAM_1
        } else if (isTeam2TargetReached) {
            isGameOver = true
            winnerTeam = Team.TEAM_2
        } else {
            isGameOver = false
            winnerTeam = null
        }

        return GameScoreSummary(
            team1TotalScore = team1Total,
            team2TotalScore = team2Total,
            currentRoundNumber = roundCount + 1,
            leadingTeam = leadingTeam,
            scoreDifference = scoreDiff,
            isGameOver = isGameOver,
            winnerTeam = winnerTeam
        )
    }
}
