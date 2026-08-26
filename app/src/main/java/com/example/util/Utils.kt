package com.example.util

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.model.GameMode
import com.example.domain.model.Team
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PersianUtils {
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(text: String): String {
        val builder = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Int): String {
        val sign = if (number < 0) "-" else ""
        return sign + toPersianDigits(Math.abs(number).toString())
    }

    fun formatScoreDelta(delta: Int): String {
        return when {
            delta > 0 -> "+${toPersianDigits(delta)}"
            delta < 0 -> "-${toPersianDigits(Math.abs(delta))}"
            else -> "۰"
        }
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
        return toPersianDigits(sdf.format(Date(timestamp)))
    }
}

object SoundAndHapticHelper {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
        } catch (_: Exception) {}
    }

    fun playClickSound(context: Context, soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Exception) {}
    }

    fun playSuccessSound(context: Context, soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
        } catch (_: Exception) {}
    }

    fun playAlertSound(context: Context, soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        } catch (_: Exception) {}
    }

    fun playShelemFanfare(context: Context, soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
        } catch (_: Exception) {}
    }

    fun triggerVibration(context: Context, strong: Boolean = false) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (it.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val duration = if (strong) 80L else 30L
                        val amplitude = if (strong) VibrationEffect.DEFAULT_AMPLITUDE else 100
                        it.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(if (strong) 80L else 30L)
                    }
                }
            }
        } catch (_: Exception) {}
    }
}

object ShareHelper {
    fun shareGameResult(
        context: Context,
        game: GameEntity,
        rounds: List<RoundEntity>,
        team1Score: Int,
        team2Score: Int
    ) {
        val gameMode = GameMode.fromString(game.gameMode)
        val winnerName = when (game.winnerTeam) {
            Team.TEAM_1.name -> game.team1Name
            Team.TEAM_2.name -> game.team2Name
            else -> if (team1Score > team2Score) game.team1Name else game.team2Name
        }

        val shareText = buildString {
            appendLine("🃏 نتیجه بازی شلم — اپلیکیشن «شلمیار» 🃏")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("🏆 برنده بازی: $winnerName")
            appendLine("🎮 نوع بازی: ${gameMode.displayName}")
            appendLine("🎯 هدف امتیاز: ${PersianUtils.toPersianDigits(game.targetScore)}")
            appendLine("⏱ تعداد دورها: ${PersianUtils.toPersianDigits(rounds.size)}")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("📊 نتایج نهایی:")
            appendLine("🔹 ${game.team1Name}: ${PersianUtils.toPersianDigits(team1Score)} امتیاز")
            appendLine("🔸 ${game.team2Name}: ${PersianUtils.toPersianDigits(team2Score)} امتیاز")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            if (rounds.isNotEmpty()) {
                appendLine("📝 خلاصه دورها:")
                rounds.forEach { round ->
                    val hakimName = if (round.hakimTeam == Team.TEAM_1.name) game.team1Name else game.team2Name
                    val statusEmoji = when {
                        round.isShelem -> "👑 شلم"
                        round.isNegativeShelem -> "💥 شلم منفی"
                        round.isYasa -> "⚠️ یاسا"
                        else -> "✅ موفق"
                    }
                    appendLine("دور ${PersianUtils.toPersianDigits(round.roundNumber)}: حاکم: $hakimName | خوانده: ${PersianUtils.toPersianDigits(round.bid)} | $statusEmoji (${PersianUtils.formatScoreDelta(round.team1ScoreDelta)} / ${PersianUtils.formatScoreDelta(round.team2ScoreDelta)})")
                }
                appendLine("━━━━━━━━━━━━━━━━━━━")
            }
            appendLine("📱 ثبت‌شده با اپلیکیشن شلمیار")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "اشتراک‌گذاری نتیجه بازی شلم")
        context.startActivity(shareIntent)
    }
}
