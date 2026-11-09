package com.sofiaverdugo.fer_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FerBlue = Color(0xFF1A3989)
private val FerGray = Color(0xFF5F5F5F)

private val FerColorScheme = lightColorScheme(
    primary = FerBlue,
    onPrimary = Color.White,
    secondary = FerGray,
    tertiary = FerGray,
    background = Color(0xFFF0F0F5),
    surface = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = FerGray
)

@Composable
fun FER_appTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FerColorScheme,
        typography = Typography,
        content = content
    )
}