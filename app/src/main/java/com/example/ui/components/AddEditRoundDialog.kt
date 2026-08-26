package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import com.example.domain.engine.ScoreEngine
import com.example.domain.model.GameMode
import com.example.domain.model.RoundInput
import com.example.domain.model.Team
import com.example.ui.theme.ShelemGoldColor
import com.example.ui.theme.SuccessGreenColor
import com.example.ui.theme.Team1Color
import com.example.ui.theme.Team2Color
import com.example.ui.theme.YasaRedColor
import com.example.util.PersianUtils

@Composable
fun AddEditRoundDialog(
    game: GameEntity,
    existingRound: RoundEntity? = null,
    onDismiss: () -> Unit,
    onSubmit: (RoundInput) -> Unit
) {
    val gameMode = GameMode.fromString(game.gameMode)
    val totalPoints = ScoreEngine.getTotalPoints(gameMode)

    var hakimTeam by remember {
        mutableStateOf(
            if (existingRound?.hakimTeam == Team.TEAM_2.name) Team.TEAM_2 else Team.TEAM_1
        )
    }
    var bid by remember { mutableIntStateOf(existingRound?.bid ?: 100) }
    var hakimEarnedPoints by remember {
        mutableIntStateOf(existingRound?.hakimEarnedPoints ?: 100)
    }
    var isShelemDeclared by remember { mutableStateOf(existingRound?.isShelem ?: false) }

    // Live preview calculation using ScoreEngine
    val roundInput = remember(hakimTeam, bid, hakimEarnedPoints, isShelemDeclared) {
        RoundInput(
            hakimTeam = hakimTeam,
            bid = bid,
            hakimEarnedPoints = hakimEarnedPoints,
            isShelemDeclared = isShelemDeclared
        )
    }

    val validationResult = remember(roundInput, gameMode) {
        ScoreEngine.validateInput(roundInput, gameMode)
    }

    val previewResult = remember(roundInput, gameMode, game.yasaEnabled) {
        ScoreEngine.calculateRound(roundInput, gameMode, game.yasaEnabled)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("add_edit_round_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingRound == null) "ثبت دور جدید" else "ویرایش دور ${PersianUtils.toPersianDigits(existingRound.roundNumber)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp).testTag("close_round_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Hakim Team Selector
                Text(
                    text = "تیم حاکم (خواننده قرارداد):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamPickerCard(
                        teamName = game.team1Name,
                        teamColor = Team1Color,
                        isSelected = hakimTeam == Team.TEAM_1,
                        onClick = { hakimTeam = Team.TEAM_1 },
                        modifier = Modifier.weight(1f).testTag("select_team1_hakim")
                    )
                    TeamPickerCard(
                        teamName = game.team2Name,
                        teamColor = Team2Color,
                        isSelected = hakimTeam == Team.TEAM_2,
                        onClick = { hakimTeam = Team.TEAM_2 },
                        modifier = Modifier.weight(1f).testTag("select_team2_hakim")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Bid (خوانده) Stepper & Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مقدار تعهد (خوانده):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${PersianUtils.toPersianDigits(bid)} امتیاز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledStepperButton(
                        icon = Icons.Default.Remove,
                        onClick = { if (bid > 100) bid -= 5 },
                        enabled = bid > 100,
                        tag = "bid_minus_button"
                    )

                    Slider(
                        value = bid.toFloat(),
                        onValueChange = { bid = (it / 5).toInt() * 5 },
                        valueRange = 100f..totalPoints.toFloat(),
                        steps = (totalPoints - 100) / 5 - 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .testTag("bid_slider")
                    )

                    FilledStepperButton(
                        icon = Icons.Default.Add,
                        onClick = { if (bid < totalPoints) bid += 5 },
                        enabled = bid < totalPoints,
                        tag = "bid_plus_button"
                    )
                }

                // Quick Bid Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickBids = if (gameMode == GameMode.WITHOUT_JOKER) {
                        listOf(100, 105, 110, 120, 130, 140, 150, 165)
                    } else {
                        listOf(100, 110, 120, 130, 140, 150, 165, 200)
                    }
                    quickBids.forEach { qBid ->
                        FilterChip(
                            selected = bid == qBid,
                            onClick = { bid = qBid },
                            label = { Text(PersianUtils.toPersianDigits(qBid)) },
                            modifier = Modifier.testTag("quick_bid_$qBid")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Actual Earned Points by Hakim
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "امتیاز کسب‌شده توسط حاکم:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${PersianUtils.toPersianDigits(hakimEarnedPoints)} از ${PersianUtils.toPersianDigits(totalPoints)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (hakimEarnedPoints >= bid) SuccessGreenColor else YasaRedColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stepper Row for Earned Points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledStepperButton(
                        icon = Icons.Default.Remove,
                        onClick = { if (hakimEarnedPoints > 0) hakimEarnedPoints -= 5 },
                        enabled = hakimEarnedPoints > 0,
                        tag = "earned_minus_button"
                    )

                    Slider(
                        value = hakimEarnedPoints.toFloat(),
                        onValueChange = { hakimEarnedPoints = (it / 5).toInt() * 5 },
                        valueRange = 0f..totalPoints.toFloat(),
                        steps = (totalPoints / 5) - 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .testTag("earned_slider")
                    )

                    FilledStepperButton(
                        icon = Icons.Default.Add,
                        onClick = { if (hakimEarnedPoints < totalPoints) hakimEarnedPoints += 5 },
                        enabled = hakimEarnedPoints < totalPoints,
                        tag = "earned_plus_button"
                    )
                }

                // Quick Earned Points Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickEarned = if (gameMode == GameMode.WITHOUT_JOKER) {
                        listOf(0, 80, 100, 105, 110, 120, 130, 140, 150, 165)
                    } else {
                        listOf(0, 100, 110, 120, 130, 140, 150, 165, 200)
                    }
                    quickEarned.forEach { qPoints ->
                        FilterChip(
                            selected = hakimEarnedPoints == qPoints,
                            onClick = {
                                hakimEarnedPoints = qPoints
                                if (qPoints == totalPoints) isShelemDeclared = true
                            },
                            label = {
                                Text(
                                    when (qPoints) {
                                        0 -> "۰ (شلم منفی)"
                                        totalPoints -> "${PersianUtils.toPersianDigits(qPoints)} (شلم)"
                                        else -> PersianUtils.toPersianDigits(qPoints)
                                    }
                                )
                            },
                            modifier = Modifier.testTag("quick_earned_$qPoints")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Shelem Declaration Switch
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ShelemGoldColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "اعلام شلم (۲ برابر امتیاز قرارداد)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Switch(
                            checked = isShelemDeclared || hakimEarnedPoints == totalPoints,
                            onCheckedChange = { isShelemDeclared = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ShelemGoldColor),
                            modifier = Modifier.testTag("shelem_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Live Score Engine Preview Box
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().testTag("live_score_preview_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "پیش‌نمایش محاسبه امتیاز:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Status Tag
                            when {
                                previewResult.isShelem -> {
                                    Text(
                                        text = "👑 شلم (+۲ برابر)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ShelemGoldColor
                                    )
                                }
                                previewResult.isNegativeShelem -> {
                                    Text(
                                        text = "💥 شلم منفی",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = YasaRedColor
                                    )
                                }
                                previewResult.isYasa -> {
                                    Text(
                                        text = "⚠️ یاسا (-۲ برابر)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = YasaRedColor
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "✅ قرارداد موفق",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreenColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${game.team1Name}: ${PersianUtils.formatScoreDelta(previewResult.team1ScoreDelta)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (previewResult.team1ScoreDelta < 0) YasaRedColor else Team1Color
                            )
                            Text(
                                text = "${game.team2Name}: ${PersianUtils.formatScoreDelta(previewResult.team2ScoreDelta)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (previewResult.team2ScoreDelta < 0) YasaRedColor else Team2Color
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = previewResult.explanationPersian,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Inline Validation Error if any
                if (validationResult is ScoreEngine.ValidationResult.Invalid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = validationResult.errorMessagePersian,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Submit / Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("cancel_round_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = {
                            if (validationResult is ScoreEngine.ValidationResult.Valid) {
                                onSubmit(roundInput)
                            }
                        },
                        enabled = validationResult is ScoreEngine.ValidationResult.Valid,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("save_round_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ثبت دور")
                    }
                }
            }
        }
    }
}

@Composable
fun TeamPickerCard(
    teamName: String,
    teamColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) teamColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) teamColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = teamColor,
                    modifier = Modifier.size(18.dp).padding(end = 4.dp)
                )
            }
            Text(
                text = teamName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) teamColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FilledStepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    tag: String
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(40.dp).testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
