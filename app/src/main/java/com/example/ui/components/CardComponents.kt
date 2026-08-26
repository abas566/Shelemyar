package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.model.GameScoreSummary
import com.example.domain.model.Team
import com.example.ui.theme.ShelemGoldColor
import com.example.ui.theme.SuccessGreenColor
import com.example.ui.theme.Team1Color
import com.example.ui.theme.Team2Color
import com.example.ui.theme.YasaRedColor
import com.example.util.PersianUtils

@Composable
fun ScoreboardCard(
    game: GameEntity,
    summary: GameScoreSummary,
    modifier: Modifier = Modifier
) {
    val target = game.targetScore
    val t1Progress = (summary.team1TotalScore.coerceAtLeast(0).toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val t2Progress = (summary.team2TotalScore.coerceAtLeast(0).toFloat() / target.toFloat()).coerceIn(0f, 1f)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scoreboard_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Round Number & Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "دور ${PersianUtils.toPersianDigits(summary.currentRoundNumber)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "هدف: ${PersianUtils.toPersianDigits(target)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Score Area: Two Columns (Team 1 vs Team 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1 Score Box
                TeamScoreBox(
                    teamName = game.team1Name,
                    score = summary.team1TotalScore,
                    teamColor = Team1Color,
                    isLeading = summary.leadingTeam == Team.TEAM_1,
                    progress = t1Progress,
                    modifier = Modifier.weight(1f).testTag("team1_score_box")
                )

                // Divider / VS badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (summary.scoreDifference > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اختلاف: ${PersianUtils.toPersianDigits(summary.scoreDifference)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Team 2 Score Box
                TeamScoreBox(
                    teamName = game.team2Name,
                    score = summary.team2TotalScore,
                    teamColor = Team2Color,
                    isLeading = summary.leadingTeam == Team.TEAM_2,
                    progress = t2Progress,
                    modifier = Modifier.weight(1f).testTag("team2_score_box")
                )
            }
        }
    }
}

@Composable
fun TeamScoreBox(
    teamName: String,
    score: Int,
    teamColor: Color,
    isLeading: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isLeading) androidx.compose.foundation.BorderStroke(2.dp, teamColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLeading) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "پیشتاز",
                        tint = ShelemGoldColor,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                }
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = teamColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = PersianUtils.toPersianDigits(score),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (score < 0) YasaRedColor else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = teamColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun RoundHistoryItem(
    round: RoundEntity,
    team1Name: String,
    team2Name: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hakimName = if (round.hakimTeam == Team.TEAM_1.name) team1Name else team2Name
    val isTeam1Hakim = round.hakimTeam == Team.TEAM_1.name

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("round_item_${round.roundNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Round Number, Status Badge, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = PersianUtils.toPersianDigits(round.roundNumber),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Status Badge
                    when {
                        round.isShelem -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShelemGoldColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShelemGoldColor)
                            ) {
                                Text(
                                    text = "👑 شلم (+۲ برابر)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ShelemGoldColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        round.isNegativeShelem -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = YasaRedColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, YasaRedColor)
                            ) {
                                Text(
                                    text = "💥 شلم منفی",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = YasaRedColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        round.isYasa -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = YasaRedColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, YasaRedColor)
                            ) {
                                Text(
                                    text = "⚠️ یاسا (-۲ برابر)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = YasaRedColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreenColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "✅ قرارداد موفق",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreenColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Edit & Delete Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp).testTag("edit_round_${round.roundNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ویرایش دور",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_round_${round.roundNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف دور",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details: Hakim name, Bid, Hakim Points
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "حاکم: $hakimName | تعهد: ${PersianUtils.toPersianDigits(round.bid)} | کسب‌شده: ${PersianUtils.toPersianDigits(round.hakimEarnedPoints)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score changes row: Team 1 change | Team 2 change
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$team1Name: ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Team1Color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = PersianUtils.formatScoreDelta(round.team1ScoreDelta),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (round.team1ScoreDelta < 0) YasaRedColor else SuccessGreenColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$team2Name: ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Team2Color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = PersianUtils.formatScoreDelta(round.team2ScoreDelta),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (round.team2ScoreDelta < 0) YasaRedColor else SuccessGreenColor
                    )
                }
            }
        }
    }
}
