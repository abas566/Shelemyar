package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.GameMode
import com.example.domain.model.GameSettings
import com.example.ui.theme.Team1Color
import com.example.ui.theme.Team2Color
import com.example.util.PersianUtils

@Composable
fun NewGameDialog(
    currentSettings: GameSettings,
    onDismiss: () -> Unit,
    onStartGame: (team1Name: String, team2Name: String, gameMode: GameMode, targetScore: Int, yasaEnabled: Boolean) -> Unit
) {
    var selectedMode by remember { mutableStateOf(GameMode.WITHOUT_JOKER) }
    var team1Name by remember { mutableStateOf("گروه اول") }
    var team2Name by remember { mutableStateOf("گروه دوم") }
    var yasaEnabled by remember { mutableStateOf(currentSettings.yasaEnabled) }
    var targetScore by remember(selectedMode) {
        mutableIntStateOf(
            if (selectedMode == GameMode.WITHOUT_JOKER) currentSettings.targetScoreWithoutJoker else currentSettings.targetScoreWithJoker
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("new_game_dialog"),
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شروع بازی جدید",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp).testTag("close_new_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Game Mode Selector
                Text(
                    text = "نوع بازی را انتخاب کنید:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameModeCard(
                        title = "بدون جوکر",
                        subtitle = "مجموع ۱۶۵ امتیاز",
                        isSelected = selectedMode == GameMode.WITHOUT_JOKER,
                        onClick = {
                            selectedMode = GameMode.WITHOUT_JOKER
                            targetScore = currentSettings.targetScoreWithoutJoker
                        },
                        modifier = Modifier.weight(1f).testTag("mode_without_joker")
                    )

                    GameModeCard(
                        title = "با جوکر",
                        subtitle = "مجموع ۲۰۰ امتیاز",
                        isSelected = selectedMode == GameMode.WITH_JOKER,
                        onClick = {
                            selectedMode = GameMode.WITH_JOKER
                            targetScore = currentSettings.targetScoreWithJoker
                        },
                        modifier = Modifier.weight(1f).testTag("mode_with_joker")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Team Names
                Text(
                    text = "نام تیم‌ها:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = team1Name,
                            onValueChange = { team1Name = it },
                            label = { Text("نام گروه اول") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("team1_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = team2Name,
                            onValueChange = { team2Name = it },
                            label = { Text("نام گروه دوم") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("team2_name_input")
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val temp = team1Name
                            team1Name = team2Name
                            team2Name = temp
                        },
                        modifier = Modifier.testTag("swap_teams_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "جابجایی نام تیم‌ها",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Target Score Presets
                Text(
                    text = "امتیاز پایان بازی (هدف):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = if (selectedMode == GameMode.WITHOUT_JOKER) {
                        listOf(165, 330, 660, 1165)
                    } else {
                        listOf(200, 400, 660, 1200)
                    }
                    presets.forEach { preset ->
                        FilterChip(
                            selected = targetScore == preset,
                            onClick = { targetScore = preset },
                            label = { Text(PersianUtils.toPersianDigits(preset)) },
                            modifier = Modifier.testTag("target_preset_$preset")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Yasa Rule Switch
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "قانون یاسا (دو برابر منفی)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "در صورت افتادن قرارداد، نمره منفی ۲ برابر می‌شود",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = yasaEnabled,
                            onCheckedChange = { yasaEnabled = it },
                            modifier = Modifier.testTag("yasa_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp).testTag("cancel_new_game_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف")
                    }

                    Button(
                        onClick = {
                            val t1 = team1Name.trim().ifEmpty { "گروه اول" }
                            val t2 = team2Name.trim().ifEmpty { "گروه دوم" }
                            onStartGame(t1, t2, selectedMode, targetScore, yasaEnabled)
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("start_game_confirm_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).padding(end = 4.dp)
                        )
                        Text("شروع بازی")
                    }
                }
            }
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
