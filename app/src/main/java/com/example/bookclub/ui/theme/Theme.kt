package com.example.bookclub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Create a composition local for accessing our gradient brushes throughout the app
val LocalGradients = staticCompositionLocalOf {
    AppGradients(
        backgroundGradient = Brush.verticalGradient(listOf(GradientStart, GradientEnd))
    )
}

// Custom gradients holder class
data class AppGradients(
    val backgroundGradient: Brush
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = Gold,
    tertiary = MediumGray,
    background = DarkText,
    surface = DarkText,
    onPrimary = White,
    onSecondary = DarkText,
    onTertiary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    secondary = Gold,
    tertiary = MediumGray,
    background = White, // We'll use this as a base with gradients on top
    surface = GlassWhite, // Using semi-transparent surface for glassy effect
    surfaceVariant = GlassGray, // Using semi-transparent surface variant for glassy effect
    onPrimary = White,
    onSecondary = DarkText,
    onTertiary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkText
)

@Composable
fun BookClubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom color scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    // Define our gradients
    val gradients = AppGradients(
        backgroundGradient = Brush.verticalGradient(
            colors = listOf(
                Color.White, 
                Color.White.copy(alpha = 0.98f)
            )
        )
    )
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make status bar transparent with light icons
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Provide our gradients through composition local
    androidx.compose.runtime.CompositionLocalProvider(
        LocalGradients provides gradients
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}