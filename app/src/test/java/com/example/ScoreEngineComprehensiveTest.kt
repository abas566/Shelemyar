package com.example

import com.example.domain.engine.ScoreEngine
import com.example.domain.model.GameMode
import com.example.domain.model.RoundContractStatus
import com.example.domain.model.RoundInput
import com.example.domain.model.Team
import com.example.util.PersianUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreEngineComprehensiveTest {

    // ==========================================
    // GROUP 1: Without Joker - Successful Bids (10 Tests)
    // ==========================================

    @Test
    fun test01_withoutJoker_exactBid_100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 100)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(100, result.team1ScoreDelta)
        assertEquals(65, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
        assertFalse(result.isYasa)
        assertFalse(result.isShelem)
    }

    @Test
    fun test02_withoutJoker_bid100_earned120() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 120)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(120, result.team1ScoreDelta)
        assertEquals(45, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test03_withoutJoker_bid120_earned120() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 120)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(120, result.team1ScoreDelta)
        assertEquals(45, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test04_withoutJoker_bid125_earned130() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 125, hakimEarnedPoints = 130)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(35, result.team1ScoreDelta)
        assertEquals(130, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test05_withoutJoker_bid140_earned140() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 140, hakimEarnedPoints = 140)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(140, result.team1ScoreDelta)
        assertEquals(25, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test06_withoutJoker_bid150_earned155() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 150, hakimEarnedPoints = 155)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(10, result.team1ScoreDelta)
        assertEquals(155, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test07_withoutJoker_bid160_earned160() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 160, hakimEarnedPoints = 160)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(160, result.team1ScoreDelta)
        assertEquals(5, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test08_withoutJoker_bid115_earned115() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 115, hakimEarnedPoints = 115)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(50, result.team1ScoreDelta)
        assertEquals(115, result.team2ScoreDelta)
    }

    @Test
    fun test09_withoutJoker_bid135_earned140() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 135, hakimEarnedPoints = 140)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(140, result.team1ScoreDelta)
        assertEquals(25, result.team2ScoreDelta)
    }

    @Test
    fun test10_withoutJoker_bid105_earned110() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 105, hakimEarnedPoints = 110)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(110, result.team1ScoreDelta)
        assertEquals(55, result.team2ScoreDelta)
    }

    // ==========================================
    // GROUP 2: Without Joker - Failed Bids & Yasa (10 Tests)
    // ==========================================

    @Test
    fun test11_withoutJoker_yasaActive_bid100_earned95() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 95)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-200, result.team1ScoreDelta) // -2 * 100
        assertEquals(70, result.team2ScoreDelta)   // 165 - 95
        assertEquals(RoundContractStatus.FALL_YASA, result.status)
        assertTrue(result.isYasa)
    }

    @Test
    fun test12_withoutJoker_yasaActive_bid120_earned100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 100)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-240, result.team1ScoreDelta) // -2 * 120
        assertEquals(65, result.team2ScoreDelta)
        assertTrue(result.isYasa)
    }

    @Test
    fun test13_withoutJoker_yasaActive_bid140_earned135() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 140, hakimEarnedPoints = 135)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(30, result.team1ScoreDelta)
        assertEquals(-280, result.team2ScoreDelta) // -2 * 140
        assertTrue(result.isYasa)
    }

    @Test
    fun test14_withoutJoker_yasaDisabled_bid100_earned90() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 90)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = false)

        assertEquals(-100, result.team1ScoreDelta) // -1 * 100
        assertEquals(75, result.team2ScoreDelta)
        assertFalse(result.isYasa)
    }

    @Test
    fun test15_withoutJoker_yasaDisabled_bid120_earned80() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 80)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = false)

        assertEquals(-120, result.team1ScoreDelta)
        assertEquals(85, result.team2ScoreDelta)
        assertFalse(result.isYasa)
    }

    @Test
    fun test16_withoutJoker_yasaActive_bid150_earned100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 150, hakimEarnedPoints = 100)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-300, result.team1ScoreDelta) // -2 * 150
        assertEquals(65, result.team2ScoreDelta)
    }

    @Test
    fun test17_withoutJoker_yasaActive_bid160_earned155() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 160, hakimEarnedPoints = 155)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(10, result.team1ScoreDelta)
        assertEquals(-320, result.team2ScoreDelta)
    }

    @Test
    fun test18_withoutJoker_yasaActive_bid110_earned50() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 110, hakimEarnedPoints = 50)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-220, result.team1ScoreDelta)
        assertEquals(115, result.team2ScoreDelta)
    }

    @Test
    fun test19_withoutJoker_yasaDisabled_bid140_earned130() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 140, hakimEarnedPoints = 130)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = false)

        assertEquals(35, result.team1ScoreDelta)
        assertEquals(-140, result.team2ScoreDelta)
    }

    @Test
    fun test20_withoutJoker_yasaActive_bid130_earned125() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 130, hakimEarnedPoints = 125)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-260, result.team1ScoreDelta)
        assertEquals(40, result.team2ScoreDelta)
    }

    // ==========================================
    // GROUP 3: Shelem & Negative Shelem (10 Tests)
    // ==========================================

    @Test
    fun test21_withoutJoker_shelem_bid100_earned165() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 165)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(200, result.team1ScoreDelta) // 2 * 100
        assertEquals(0, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SHELEM, result.status)
        assertTrue(result.isShelem)
        assertFalse(result.isYasa)
    }

    @Test
    fun test22_withoutJoker_shelem_bid120_earned165() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 165)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(240, result.team1ScoreDelta) // 2 * 120
        assertEquals(0, result.team2ScoreDelta)
        assertTrue(result.isShelem)
    }

    @Test
    fun test23_withoutJoker_shelem_bid165_earned165() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 165, hakimEarnedPoints = 165)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(0, result.team1ScoreDelta)
        assertEquals(330, result.team2ScoreDelta) // 2 * 165
        assertTrue(result.isShelem)
    }

    @Test
    fun test24_withoutJoker_declaredShelem_flagTrue() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 140, hakimEarnedPoints = 165, isShelemDeclared = true)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(280, result.team1ScoreDelta) // 2 * 140
        assertEquals(0, result.team2ScoreDelta)
        assertTrue(result.isShelem)
    }

    @Test
    fun test25_withoutJoker_negativeShelem_yasaActive() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 0)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(-200, result.team1ScoreDelta) // Hakim penalized -2 * 100
        assertEquals(330, result.team2ScoreDelta)  // Opponent gets 2 * 165
        assertEquals(RoundContractStatus.NEGATIVE_SHELEM, result.status)
        assertTrue(result.isNegativeShelem)
    }

    @Test
    fun test26_withoutJoker_negativeShelem_bid120_yasaActive() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 120, hakimEarnedPoints = 0)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = true)

        assertEquals(330, result.team1ScoreDelta)
        assertEquals(-240, result.team2ScoreDelta)
        assertTrue(result.isNegativeShelem)
    }

    @Test
    fun test27_withoutJoker_negativeShelem_yasaDisabled() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 0)
        val result = ScoreEngine.calculateRound(input, GameMode.WITHOUT_JOKER, yasaEnabled = false)

        assertEquals(-100, result.team1ScoreDelta)
        assertEquals(330, result.team2ScoreDelta)
        assertTrue(result.isNegativeShelem)
    }

    @Test
    fun test28_withJoker_shelem_bid100_earned200() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 200)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(200, result.team1ScoreDelta)
        assertEquals(0, result.team2ScoreDelta)
        assertTrue(result.isShelem)
    }

    @Test
    fun test29_withJoker_shelem_bid200_earned200() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 200, hakimEarnedPoints = 200)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(400, result.team1ScoreDelta) // 2 * 200
        assertEquals(0, result.team2ScoreDelta)
        assertTrue(result.isShelem)
    }

    @Test
    fun test30_withJoker_negativeShelem_bid100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 0)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(-200, result.team1ScoreDelta)
        assertEquals(400, result.team2ScoreDelta) // 2 * 200
        assertTrue(result.isNegativeShelem)
    }

    // ==========================================
    // GROUP 4: With Joker Games (10 Tests)
    // ==========================================

    @Test
    fun test31_withJoker_totalPointsIs200() {
        assertEquals(200, ScoreEngine.getTotalPoints(GameMode.WITH_JOKER))
        assertEquals(20, ScoreEngine.JOKER_RED_VALUE)
        assertEquals(15, ScoreEngine.JOKER_BLACK_VALUE)
    }

    @Test
    fun test32_withJoker_successBid100_earned100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 100)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(100, result.team1ScoreDelta)
        assertEquals(100, result.team2ScoreDelta)
        assertEquals(RoundContractStatus.SUCCESS, result.status)
    }

    @Test
    fun test33_withJoker_successBid120_earned140() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 140)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(140, result.team1ScoreDelta)
        assertEquals(60, result.team2ScoreDelta) // 200 - 140
    }

    @Test
    fun test34_withJoker_successBid150_earned165() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 150, hakimEarnedPoints = 165)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(35, result.team1ScoreDelta) // 200 - 165
        assertEquals(165, result.team2ScoreDelta)
    }

    @Test
    fun test35_withJoker_yasaActive_bid100_earned95() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 95)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(-200, result.team1ScoreDelta)
        assertEquals(105, result.team2ScoreDelta)
        assertTrue(result.isYasa)
    }

    @Test
    fun test36_withJoker_yasaActive_bid150_earned140() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 150, hakimEarnedPoints = 140)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(60, result.team1ScoreDelta)
        assertEquals(-300, result.team2ScoreDelta) // -2 * 150
        assertTrue(result.isYasa)
    }

    @Test
    fun test37_withJoker_yasaDisabled_bid120_earned100() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 120, hakimEarnedPoints = 100)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = false)

        assertEquals(-120, result.team1ScoreDelta)
        assertEquals(100, result.team2ScoreDelta)
        assertFalse(result.isYasa)
    }

    @Test
    fun test38_withJoker_bid180_earned180() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 180, hakimEarnedPoints = 180)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(180, result.team1ScoreDelta)
        assertEquals(20, result.team2ScoreDelta)
    }

    @Test
    fun test39_withJoker_bid165_earned170() {
        val input = RoundInput(hakimTeam = Team.TEAM_2, bid = 165, hakimEarnedPoints = 170)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(30, result.team1ScoreDelta)
        assertEquals(170, result.team2ScoreDelta)
    }

    @Test
    fun test40_withJoker_shelem_bid150_earned200() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 150, hakimEarnedPoints = 200)
        val result = ScoreEngine.calculateRound(input, GameMode.WITH_JOKER, yasaEnabled = true)

        assertEquals(300, result.team1ScoreDelta) // 2 * 150
        assertEquals(0, result.team2ScoreDelta)
        assertTrue(result.isShelem)
    }

    // ==========================================
    // GROUP 5: Validations (6 Tests)
    // ==========================================

    @Test
    fun test41_validation_bidBelow100_isInvalid() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 95, hakimEarnedPoints = 100)
        val result = ScoreEngine.validateInput(input, GameMode.WITHOUT_JOKER)
        assertTrue(result is ScoreEngine.ValidationResult.Invalid)
    }

    @Test
    fun test42_validation_bidExceedsTotal_isInvalid() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 170, hakimEarnedPoints = 100)
        val result = ScoreEngine.validateInput(input, GameMode.WITHOUT_JOKER)
        assertTrue(result is ScoreEngine.ValidationResult.Invalid)
    }

    @Test
    fun test43_validation_bidNotMultipleOf5_isInvalid() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 103, hakimEarnedPoints = 100)
        val result = ScoreEngine.validateInput(input, GameMode.WITHOUT_JOKER)
        assertTrue(result is ScoreEngine.ValidationResult.Invalid)
    }

    @Test
    fun test44_validation_earnedNegative_isInvalid() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = -5)
        val result = ScoreEngine.validateInput(input, GameMode.WITHOUT_JOKER)
        assertTrue(result is ScoreEngine.ValidationResult.Invalid)
    }

    @Test
    fun test45_validation_earnedExceedsTotal_isInvalid() {
        val input = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 170)
        val result = ScoreEngine.validateInput(input, GameMode.WITHOUT_JOKER)
        assertTrue(result is ScoreEngine.ValidationResult.Invalid)
    }

    @Test
    fun test46_validation_validInputs_passes() {
        val input1 = RoundInput(hakimTeam = Team.TEAM_1, bid = 100, hakimEarnedPoints = 100)
        val result1 = ScoreEngine.validateInput(input1, GameMode.WITHOUT_JOKER)
        assertTrue(result1 is ScoreEngine.ValidationResult.Valid)

        val input2 = RoundInput(hakimTeam = Team.TEAM_2, bid = 200, hakimEarnedPoints = 200)
        val result2 = ScoreEngine.validateInput(input2, GameMode.WITH_JOKER)
        assertTrue(result2 is ScoreEngine.ValidationResult.Valid)
    }

    // ==========================================
    // GROUP 6: Multi-round Game Summaries & Full Match Simulation (6 Tests)
    // ==========================================

    @Test
    fun test47_summarizeGame_emptyRounds() {
        val summary = ScoreEngine.summarizeGame(emptyList(), targetScore = 165)
        assertEquals(0, summary.team1TotalScore)
        assertEquals(0, summary.team2TotalScore)
        assertEquals(1, summary.currentRoundNumber)
        assertNull(summary.leadingTeam)
        assertFalse(summary.isGameOver)
        assertNull(summary.winnerTeam)
    }

    @Test
    fun test48_summarizeGame_team1ReachesTarget() {
        val round1 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_1, bid = 100, hakimEarnedPoints = 100),
            GameMode.WITHOUT_JOKER
        )
        val round2 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_1, bid = 100, hakimEarnedPoints = 100),
            GameMode.WITHOUT_JOKER
        )
        val summary = ScoreEngine.summarizeGame(listOf(round1, round2), targetScore = 165)

        assertEquals(200, summary.team1TotalScore)
        assertEquals(130, summary.team2TotalScore)
        assertEquals(3, summary.currentRoundNumber)
        assertEquals(Team.TEAM_1, summary.leadingTeam)
        assertTrue(summary.isGameOver)
        assertEquals(Team.TEAM_1, summary.winnerTeam)
    }

    @Test
    fun test49_summarizeGame_bothCrossTarget_higherWins() {
        val round1 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_1, bid = 100, hakimEarnedPoints = 100),
            GameMode.WITHOUT_JOKER
        )
        val round2 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_2, bid = 100, hakimEarnedPoints = 120),
            GameMode.WITHOUT_JOKER
        )
        val summary = ScoreEngine.summarizeGame(listOf(round1, round2), targetScore = 100)

        assertEquals(145, summary.team1TotalScore) // 100 + 45
        assertEquals(185, summary.team2TotalScore) // 65 + 120
        assertTrue(summary.isGameOver)
        assertEquals(Team.TEAM_2, summary.winnerTeam)
    }

    @Test
    fun test50_fullGameSimulation_withYasaAndShelem() {
        // Round 1: Team 1 bids 100 and succeeds with 105 (T1: +105, T2: +60)
        val r1 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_1, bid = 100, hakimEarnedPoints = 105),
            GameMode.WITHOUT_JOKER,
            yasaEnabled = true
        )
        assertEquals(105, r1.team1ScoreDelta)
        assertEquals(60, r1.team2ScoreDelta)

        // Round 2: Team 2 bids 120 and falls to Yasa with 100 (T1: +65, T2: -240)
        val r2 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_2, bid = 120, hakimEarnedPoints = 100),
            GameMode.WITHOUT_JOKER,
            yasaEnabled = true
        )
        assertEquals(65, r2.team1ScoreDelta)
        assertEquals(-240, r2.team2ScoreDelta)

        // Round 3: Team 1 bids 120 and makes SHELEM with 165 (T1: +240, T2: 0)
        val r3 = ScoreEngine.calculateRound(
            RoundInput(Team.TEAM_1, bid = 120, hakimEarnedPoints = 165),
            GameMode.WITHOUT_JOKER,
            yasaEnabled = true
        )
        assertEquals(240, r3.team1ScoreDelta)
        assertEquals(0, r3.team2ScoreDelta)

        val summary = ScoreEngine.summarizeGame(listOf(r1, r2, r3), targetScore = 330)

        assertEquals(410, summary.team1TotalScore)  // 105 + 65 + 240
        assertEquals(-180, summary.team2TotalScore) // 60 - 240 + 0
        assertEquals(4, summary.currentRoundNumber)
        assertEquals(Team.TEAM_1, summary.leadingTeam)
        assertTrue(summary.isGameOver)
        assertEquals(Team.TEAM_1, summary.winnerTeam)
    }

    @Test
    fun test51_persianUtilsFormatting() {
        assertEquals("۱۲۳", PersianUtils.toPersianDigits(123))
        assertEquals("-۲۴۰", PersianUtils.toPersianDigits(-240))
        assertEquals("+۱۲۰", PersianUtils.formatScoreDelta(120))
        assertEquals("-۱۰۰", PersianUtils.formatScoreDelta(-100))
        assertEquals("۰", PersianUtils.formatScoreDelta(0))
    }

    @Test
    fun test52_teamOtherFunctionality() {
        assertEquals(Team.TEAM_2, Team.TEAM_1.other())
        assertEquals(Team.TEAM_1, Team.TEAM_2.other())
        assertEquals("گروه اول", Team.TEAM_1.defaultPersianName)
        assertEquals("گروه دوم", Team.TEAM_2.defaultPersianName)
    }
}
