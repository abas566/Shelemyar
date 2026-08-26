package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.model.GameScoreSummary
import com.example.domain.model.Team
import com.example.ui.theme.ShelemGoldColor
import com.example.ui.theme.Team1Color
import com.example.ui.theme.Team2Color
import com.example.util.PersianUtils
import com.example.util.ShareHelper

@Composable
fun WinnerCelebrationDialog(
    game: GameEntity,
    summary: GameScoreSummary,
    rounds: List<RoundEntity>,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit
) {
    val context = LocalContext.current
    val winnerName = when (summary.winnerTeam) {
        Team.TEAM_1 -> game.team1Name
        Team.TEAM_2 -> game.team2Name
        else -> if (summary.team1TotalScore > summary.team2TotalScore) game.team1Name else game.team2Name
    }
    val winnerColor = if (summary.winnerTeam == Team.TEAM_1) Team1Color else Team2Color

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("winner_dialog"),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy Icon Container
                Surface(
                    shape = CircleShape,
                    color = ShelemGoldColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ShelemGoldColor),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "جام قهرمانی",
                            tint = ShelemGoldColor,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🎉 تبریک به قهرمان بازی! 🎉",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = winnerName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = winnerColor,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "به حد نصاب امتیاز (${PersianUtils.toPersianDigits(game.targetScore)}) رسید و پیروز شد!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Final Scoreboard Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = game.team1Name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Team1Color
                            )
                            Text(
                                text = PersianUtils.toPersianDigits(summary.team1TotalScore),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Text(
                            text = "مقابل",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = game.team2Name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Team2Color
                            )
                            Text(
                                text = PersianUtils.toPersianDigits(summary.team2TotalScore),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Share Result & Start New Game
                Button(
                    onClick = {
                        ShareHelper.shareGameResult(
                            context = context,
                            game = game,
                            rounds = rounds,
                            team1Score = summary.team1TotalScore,
                            team2Score = summary.team2TotalScore
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("share_result_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).padding(end = 6.dp)
                    )
                    Text("اشتراک‌گذاری نتیجه بازی")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("close_winner_dialog_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("مشاهده جدول")
                    }

                    Button(
                        onClick = onNewGame,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("new_game_from_winner_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).padding(end = 4.dp)
                        )
                        Text("بازی جدید")
                    }
                }
            }
        }
    }
}
