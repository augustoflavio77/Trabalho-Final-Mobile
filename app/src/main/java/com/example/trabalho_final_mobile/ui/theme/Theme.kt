package com.example.trabalho_final_mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary              = Teal600,
    onPrimary            = Neutral0,
    primaryContainer     = Teal100,
    onPrimaryContainer   = Teal600,

    secondary            = Coral600,
    onSecondary          = Neutral0,
    secondaryContainer   = Coral100,
    onSecondaryContainer = Coral600,

    tertiary             = Teal500,
    onTertiary           = Neutral0,

    background           = Neutral50,
    onBackground         = Neutral900,

    surface              = Neutral0,
    onSurface            = Neutral900,
    surfaceVariant       = Neutral100,
    onSurfaceVariant     = Neutral500,

    outline              = Neutral200,
    outlineVariant       = Neutral100,

    error                = ErrorRed,
    onError              = Neutral0,
    errorContainer       = ErrorBg,
    onErrorContainer     = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary              = Teal400,
    onPrimary            = Neutral900,
    primaryContainer     = Teal600,
    onPrimaryContainer   = Teal100,

    secondary            = Coral500,
    onSecondary          = Neutral900,
    secondaryContainer   = Coral600,
    onSecondaryContainer = Coral100,

    tertiary             = Teal400,
    onTertiary           = Neutral900,

    background           = DarkBg,
    onBackground         = Neutral0,

    surface              = DarkSurface,
    onSurface            = Neutral0,
    surfaceVariant       = DarkSurface2,
    onSurfaceVariant     = Neutral400,

    outline              = DarkBorder,
    outlineVariant       = DarkSurface2,

    error                = ErrorRed,
    onError              = Neutral0,
    errorContainer       = Color(0xFF3F1212),
    onErrorContainer     = Coral100
)

// Cantos arredondados generosos (estilo moderno)
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun Trabalho_final_mobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sem dynamic color: mantemos a identidade visual consistente em todos os dispositivos
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}
