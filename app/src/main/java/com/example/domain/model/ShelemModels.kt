package com.example.domain.model

enum class GameMode(val displayName: String, val totalPoints: Int, val defaultTarget: Int) {
    WITHOUT_JOKER("بدون جوکر (۱۶۵ امتیاز)", 165, 165),
    WITH_JOKER("با جوکر (۲۰۰ امتیاز)", 200, 200);

    companion object {
        fun fromString(value: String): GameMode {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: WITHOUT_JOKER
        }
    }
}

enum class Team(val defaultPersianName: String) {
    TEAM_1("گروه اول"),
    TEAM_2("گروه دوم");

    fun other(): Team = if (this == TEAM_1) TEAM_2 else TEAM_1
}

enum class RoundContractStatus {
    SUCCESS,        // موفق / تعهد انجام شد
    FALL_YASA,      // یاسا / افتادن قرارداد (منفی ۲ برابر در صورت فعال بودن یاسا)
    SHELEM,         // شلم مثبت (۲ برابر قرارداد یا امتیاز کامل)
    NEGATIVE_SHELEM // شلم منفی (رقیب تمام امتیازات را برد)
}

data class RoundInput(
    val hakimTeam: Team,
    val bid: Int,
    val hakimEarnedPoints: Int,
    val isShelemDeclared: Boolean = false
)

data class RoundScoreResult(
    val hakimTeam: Team,
    val bid: Int,
    val hakimEarnedPoints: Int,
    val opponentEarnedPoints: Int,
    val team1ScoreDelta: Int,
    val team2ScoreDelta: Int,
    val status: RoundContractStatus,
    val isYasa: Boolean,
    val isShelem: Boolean,
    val isNegativeShelem: Boolean,
    val explanationPersian: String
)

data class GameScoreSummary(
    val team1TotalScore: Int,
    val team2TotalScore: Int,
    val currentRoundNumber: Int,
    val leadingTeam: Team?,
    val scoreDifference: Int,
    val isGameOver: Boolean,
    val winnerTeam: Team?
)

data class GameSettings(
    val yasaEnabled: Boolean = true,
    val targetScoreWithoutJoker: Int = 165,
    val targetScoreWithJoker: Int = 200,
    val isDarkMode: Boolean? = null, // null = system default
    val animationsEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true
)
