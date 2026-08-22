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
import androidx.compose.ui.platform.LocalContext
import com.example.data.AppFontSize
import com.example.data.LocalAppFontSize
import com.example.data.LocalAppFontScale

private val DarkColorScheme = darkColorScheme(
    primary = SvBlueDarkThemePrimary,
    secondary = SvBlueDarkThemeSecondary,
    tertiary = SvAccentCyan,
    background = SvBackgroundDark,
    surface = SvSurfaceDark,
    onPrimary = SvBackgroundDark,
    onSecondary = SvBackgroundDark,
    onBackground = SvBackgroundLight,
    onSurface = SvBackgroundLight
)

private val LightColorScheme = lightColorScheme(
    primary = SvBluePrimary,
    secondary = SvSecondary,
    tertiary = SvWhiteAccent,
    background = SvBackgroundLight,
    surface = SvSurfaceLight,
    onPrimary = SvSurfaceLight,
    onSecondary = SvSurfaceLight,
    onBackground = SvBackgroundDark,
    onSurface = SvBackgroundDark
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
