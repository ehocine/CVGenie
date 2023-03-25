package com.hocel.cvgenie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val blueBG = Color(0xFFF4F7FD)
val blueText = Color(0xFF1E3054)
val card = Color(0xFFFFFFFF)

val blue = Color(0xFF006AF6)
val blueNight = Color(0xFF147EFF)
val RedColor = Color(0xFFFE554A)

val background_content_dark = Color(0xFF222222)
val background_content_light = Color(0xFFEEEEEE)

val darkBackground = Color(0xFF121212)

val Colors.BackgroundColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blueBG else darkBackground

val Colors.TextColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blueText else Color.White

val Colors.CardColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) card else Color.Black

val Colors.ButtonColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blue else blueNight

val Colors.DividerColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blueText else Color.White

val Colors.CircularProgressColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.White else Color.Black

val Colors.ButtonTextColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.White else Color.Black

val Colors.BottomSheetBackground: Color
    @Composable
    get() = if (isSystemInDarkTheme()) background_content_dark else background_content_light