package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.AppFontSize
import com.example.data.LocalAppFontSize
import com.example.data.LocalAppFontScale

private val DarkColorScheme = darkColorScheme(
    primary = SvDarkPrimary,              // #00D4E6
    onPrimary = Color(0xFF0B1320),        // Dark contrast on bright cyan button
    primaryContainer = SvDarkSecondary,   // #0A3D62
    onPrimaryContainer = Color(0xFFE9F7F8),
    secondary = SvDarkTextSecondary,      // #9FB3C8
    onSecondary = Color(0xFF0B1320),
    secondaryContainer = SvDarkSecondary, // #0A3D62
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = SvDarkPrimary,
    onTertiary = Color(0xFF0B1320),
    background = SvDarkBackground,        // #0B1320
    onBackground = SvDarkTextPrimary,     // #FFFFFF
    surface = SvDarkSurface,              // #111D30
    onSurface = SvDarkTextPrimary,        // #FFFFFF
    surfaceVariant = SvDarkSurfaceVariant,// #182942
    onSurfaceVariant = SvDarkTextSecondary,// #9FB3C8
    outline = SvDarkOutline,              // #1E3A5F
    outlineVariant = Color(0xFF1B2C44),
    error = RedError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SvLightPrimary,              // #00C6D7
    onPrimary = Color(0xFF0B2D39),         // High contrast text on cyan
    primaryContainer = SvLightSecondary,   // #7AE7F2
    onPrimaryContainer = SvLightTextContrast, // #0B2D39
    secondary = SvLightPrimaryDark,        // #009AA8
    onSecondary = Color.White,
    secondaryContainer = SvLightSurfaceVariant,
    onSecondaryContainer = SvLightTextContrast,
    tertiary = SvLightPrimary,
    onTertiary = Color(0xFF0B2D39),
    background = SvLightBackground,        // #E9F7F8
    onBackground = SvLightTextContrast,    // #0B2D39
    surface = SvLightSurface,              // #FFFFFF
    onSurface = SvLightTextContrast,       // #0B2D39
    surfaceVariant = SvLightSurfaceVariant,// #D8F3F6
    onSurfaceVariant = Color(0xFF134556),  // Dark slate teal
    outline = SvLightOutline,              // #7BDCE6
    outlineVariant = Color(0xFFCCEBF0),
    error = RedError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    appFontSize: AppFontSize = AppFontSize.MEDIANO,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val typography = createElectoralTypography(appFontSize)

    CompositionLocalProvider(
        LocalAppFontSize provides appFontSize,
        LocalAppFontScale provides appFontSize.scaleFactor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
