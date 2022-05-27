package fhnw.emoba.freezerapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val appDarkColors = darkColors(
    //Background colors
    primary = Color(0xFF2F5CE6),
    primaryVariant = Color(0xFF17203B),
    secondary = Color(0xFF757575),
    secondaryVariant = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFFF3159),

    //Typography and icon colors
    onPrimary = gray50,
    onSecondary = gray50,
    onBackground = Color(0xFFB4B4B4),
    onSurface = Color.White,
    onError = Color.Black,
)

@SuppressLint("ConflictingOnColor")
private val appLightColors = lightColors(
    //Background colors
    primary = Color(0xFF2F5CE6),
    primaryVariant = blue50,
    secondary = Color(0xFFE2E2E2),

    secondaryVariant = Color(0xFF03DAC6),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFFF3159),

    //Typography and icon colors
    onPrimary = gray50,
    onSecondary = gray900,
    onBackground = Color(0xFF363636),
    //onBackground = Color(0xFF757575),
    onSurface = Color.Black,
    onError = Color.White,
)

@Composable
fun ThatsAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) appDarkColors else appLightColors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}