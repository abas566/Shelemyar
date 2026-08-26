package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShelemGoldColor
import com.example.ui.theme.SuccessGreenColor
import com.example.ui.theme.YasaRedColor

@Composable
fun RulesScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("rules_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "قوانین و راهنمای بازی شلم",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "راهنمای کامل امتیازدهی و قوانین معتبر شلم در شلمیار",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Overview
        item {
            RuleCard(
                title = "۱. ساختار بازی و خواندن قرارداد",
                icon = Icons.Default.Casino,
                iconColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "• شلم یک بازی ۴ نفره در قالب دو تیم ۲ نفره (هم‌دست) است.\n" +
                            "• در ابتدای هر دور، بازیکنان برای تعیین تیم حاکم (خواننده) تعهد می‌خوانند.\n" +
                            "• حداقل مقدار خوانده ۱۰۰ است و با گام‌های ۵ تایی افزایش می‌یابد.\n" +
                            "• تیمی که بالاترین عدد را بخواند، حاکم شده و خال حکم را تعیین می‌کند.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // 2. Without Joker vs With Joker
        item {
            RuleCard(
                title = "۲. تفاوت شلم بدون جوکر و با جوکر",
                icon = Icons.Default.Info,
                iconColor = MaterialTheme.colorScheme.secondary
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🃏 شلم بدون جوکر (مجموع ۱۶۵ امتیاز):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• هر آس (A): ۱۰ امتیاز (۴ × ۱۰ = ۴۰)\n" +
                                "• هر شاه (K): ۱۰ امتیاز (۴ × ۱۰ = ۴۰)\n" +
                                "• هر عدد ۱۰: ۱۰ امتیاز (۴ × ۱۰ = ۴۰)\n" +
                                "• هر عدد ۵: ۵ امتیاز (۴ × ۵ = ۲۰)\n" +
                                "• دست آخر (دست برنده پایانی): ۲۵ امتیاز\n" +
                                "مجموع: ۴۰ + ۴۰ + ۴۰ + ۲۰ + ۲۵ = ۱۶۵ امتیاز.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "🎭 شلم با جوکر (مجموع ۲۰۰ امتیاز):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "• جوکر قرمز: ۲۰ امتیاز\n" +
                                "• جوکر سیاه: ۱۵ امتیاز\n" +
                                "• همراه با امتیازات کارت‌های اصلی، مجموع هر دور دقیقاً ۲۰۰ امتیاز خواهد بود.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 3. Yasa Rule
        item {
            RuleCard(
                title = "۳. قانون یاسا (جریمه منفی دو برابر)",
                icon = Icons.Default.Warning,
                iconColor = YasaRedColor
            ) {
                Text(
                    text = "• در صورتی که تیم حاکم نتواند به اندازه تعهد (خوانده) خود امتیاز کسب کند، «می‌افتد».\n" +
                            "• در صورت فعال بودن قانون یاسا، امتیاز منفی تیم حاکم دقیقاً ۲ برابر می‌شود!\n" +
                            "• مثال: اگر قرارداد ۱۰۰ باشد و تیم بیفتد: ۲۰۰- امتیاز ثبت می‌شود.\n" +
                            "• مثال: اگر قرارداد ۱۲۰ باشد و تیم بیفتد: ۲۴۰- امتیاز ثبت می‌شود.\n" +
                            "• تیم مدافع نیز هر امتیازی که در دست‌ها جمع کرده باشد به عنوان امتیاز مثبت دریافت می‌کند.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // 4. Shelem
        item {
            RuleCard(
                title = "۴. شلم (+۲ برابر امتیاز قرارداد)",
                icon = Icons.Default.Star,
                iconColor = ShelemGoldColor
            ) {
                Text(
                    text = "• هرگاه تیم حاکم تمام دست‌ها و امتیازات آن دور (۱۶۵ یا ۲۰۰ امتیاز) را تصاحب کند، شلم رخ می‌دهد.\n" +
                            "• طبق قانون، امتیاز قرارداد دو برابر محاسبه می‌شود.\n" +
                            "• مثال: اگر قرارداد ۱۲۰ خوانده شده باشد: ۲۴۰+ امتیاز کسب می‌شود.\n" +
                            "• مثال: اگر قرارداد ۱۰۰ خوانده شده باشد: ۲۰۰+ امتیاز کسب می‌شود.\n" +
                            "• تیم حریف در حالت شلم هیچ امتیازی نمی‌گیرد (۰ امتیاز).",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // 5. Negative Shelem
        item {
            RuleCard(
                title = "۵. شلم منفی (شلم وارونه)",
                icon = Icons.Default.CheckCircle,
                iconColor = YasaRedColor
            ) {
                Text(
                    text = "• اگر تیم حاکم هیچ دست یا امتیازی نگیرد و تمام امتیازات توسط تیم رقیب برده شود، «شلم منفی» اتفاق می‌افتد.\n" +
                            "• در این حالت، تیم رقیب امتیاز شلم (۲ برابر کل دور، یعنی ۳۳۰+ یا ۴۰۰+) را می‌برد و تیم حاکم جریمه منفی سنگین دریافت می‌کند.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun RuleCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}
