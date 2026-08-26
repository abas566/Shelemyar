package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.model.GameMode
import com.example.domain.model.GameScoreSummary
import com.example.ui.components.RoundHistoryItem
import com.example.ui.components.ScoreboardCard
import com.example.ui.theme.ShelemGoldColor
import com.example.util.PersianUtils
import com.example.util.ShareHelper

@Composable
fun ScoreboardScreen(
    game: GameEntity?,
    rounds: List<RoundEntity>,
    summary: GameScoreSummary,
    onNewGameClick: () -> Unit,
    onAddRoundClick: () -> Unit,
    onEditRoundClick: (RoundEntity) -> Unit,
    onDeleteRoundClick: (Long) -> Unit,
    onUndoLastRoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var roundToDeleteId by remember { mutableStateOf<Long?>(null) }
    var showUndoConfirmDialog by remember { mutableStateOf(false) }

    if (game == null) {
        // Empty State: No active game
        EmptyGameView(onNewGameClick = onNewGameClick, modifier = modifier)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onAddRoundClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("ثبت دور جدید", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_add_round")
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
            ) {
                // 1. Top Scoreboard Banner
                item {
                    ScoreboardCard(game = game, summary = summary)
                }

                // 2. Winner Alert Bar if Game is Over
                if (summary.isGameOver) {
                    item {
                        WinnerAlertCard(
                            game = game,
                            summary = summary,
                            onShare = {
                                ShareHelper.shareGameResult(
                                    context = context,
                                    game = game,
                                    rounds = rounds,
                                    team1Score = summary.team1TotalScore,
                                    team2Score = summary.team2TotalScore
                                )
                            },
                            onNewGame = onNewGameClick
                        )
                    }
                }

                // 3. Quick Action Row (Undo, Share, New Game)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { showUndoConfirmDialog = true },
                            enabled = rounds.isNotEmpty(),
                            modifier = Modifier.weight(1f).testTag("undo_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Undo,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text("Undo", fontSize = 13.sp)
                        }

                        FilledTonalButton(
                            onClick = {
                                ShareHelper.shareGameResult(
                                    context = context,
                                    game = game,
                                    rounds = rounds,
                                    team1Score = summary.team1TotalScore,
                                    team2Score = summary.team2TotalScore
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("share_game_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text("اشتراک", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onNewGameClick,
                            modifier = Modifier.weight(1.2f).testTag("header_new_game_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text("بازی جدید", fontSize = 13.sp)
                        }
                    }
                }

                // 4. Section Title: Rounds History
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تاریخچه دورها (${PersianUtils.toPersianDigits(rounds.size)})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        val gameMode = GameMode.fromString(game.gameMode)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (game.yasaEnabled) "یاسا: فعال" else "یاسا: غیرفعال",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // 5. Rounds List or Empty Indicator
                if (rounds.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("empty_rounds_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🃏",
                                    fontSize = 40.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "هنوز دوری ثبت نشده است",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "برای شروع ثبت امتیازات، دکمه «ثبت دور جدید» را بزنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = rounds.reversed(),
                        key = { it.id }
                    ) { round ->
                        RoundHistoryItem(
                            round = round,
                            team1Name = game.team1Name,
                            team2Name = game.team2Name,
                            onEdit = { onEditRoundClick(round) },
                            onDelete = { roundToDeleteId = round.id }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (roundToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { roundToDeleteId = null },
            title = { Text("حذف دور") },
            text = { Text("آیا از حذف این دور اطمینان دارید؟ امتیازات بازی مجدداً محاسبه خواهند شد.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        roundToDeleteId?.let { onDeleteRoundClick(it) }
                        roundToDeleteId = null
                    },
                    modifier = Modifier.testTag("confirm_delete_round")
                ) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { roundToDeleteId = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Undo Confirmation Dialog
    if (showUndoConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showUndoConfirmDialog = false },
            title = { Text("بازگشت به دور قبل (Undo)") },
            text = { Text("آیا می‌خواهید آخرین دور ثبت‌شده را لغو و حذف کنید؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUndoLastRoundClick()
                        showUndoConfirmDialog = false
                    },
                    modifier = Modifier.testTag("confirm_undo_button")
                ) {
                    Text("بله، لغو شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUndoConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun WinnerAlertCard(
    game: GameEntity,
    summary: GameScoreSummary,
    onShare: () -> Unit,
    onNewGame: () -> Unit
) {
    val winnerName = when (summary.winnerTeam) {
        com.example.domain.model.Team.TEAM_1 -> game.team1Name
        com.example.domain.model.Team.TEAM_2 -> game.team2Name
        else -> if (summary.team1TotalScore > summary.team2TotalScore) game.team1Name else game.team2Name
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("winner_alert_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ShelemGoldColor.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ShelemGoldColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = ShelemGoldColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "برنده بازی: $winnerName 🏆",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = ShelemGoldColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "بازی به پایان رسید! امتیاز نهایی: ${PersianUtils.toPersianDigits(summary.team1TotalScore)} در برابر ${PersianUtils.toPersianDigits(summary.team2TotalScore)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                    Text("اشتراک‌گذاری")
                }

                OutlinedButton(
                    onClick = onNewGame,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                    Text("بازی جدید")
                }
            }
        }
    }
}

@Composable
fun EmptyGameView(
    onNewGameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("empty_game_view"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "♠️ ♥️ ♣️ ♦️", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "به شلمیار خوش آمدید",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "برای شروع ثبت و محاسبه خودکار امتیازات شلم، یک بازی جدید ایجاد کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNewGameClick,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(48.dp).testTag("start_first_game_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).padding(end = 6.dp)
                    )
                    Text("شروع بازی جدید", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
