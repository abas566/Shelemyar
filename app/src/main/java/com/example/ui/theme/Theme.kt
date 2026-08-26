package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val ShelemyarDarkColorScheme = darkColorScheme(
    primary = EmeraldDark,
    onPrimary = Color(0xFF003822),
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = OnEmeraldContainerDark,
    secondary = AmberDark,
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = OnAmberContainerDark,
    tertiary = RubyDark,
    onTertiary = Color(0xFF690005),
    tertiaryContainer = RubyContainerDark,
    onTertiaryContainer = Color(0xFFFFDAD6),
    background = BackgroundDark,
    onBackground = Color(0xFFE1E8E3),
    surface = SurfaceDark,
    onSurface = Color(0xFFE1E8E3),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC0CCC4),
    outline = Color(0xFF8A9890)
)

private val ShelemyarLightColorScheme = lightColorScheme(
    primary = EmeraldLight,
    onPrimary = OnEmerald,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = OnEmeraldContainerLight,
    secondary = AmberLight,
    onSecondary = OnAmber,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = OnAmberContainerLight,
    tertiary = RubyLight,
    onTertiary = OnRuby,
    tertiaryContainer = RubyContainerLight,
    onTertiaryContainer = Color(0xFF410002),
    background = BackgroundLight,
    onBackground = Color(0xFF161E1A),
    surface = SurfaceLight,
    onSurface = Color(0xFF161E1A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF404B45),
    outline = Color(0xFF707D76)
)

@Composable
fun ShelemyarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ShelemyarDarkColorScheme else ShelemyarLightColorScheme

    // Enforce Persian RTL across the entire application hierarchy
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
