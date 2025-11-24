package com.example.slideit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CBFB), // 대표 색상 (라이트 블루)
    secondary = Color(0xFF606060),
    tertiary = Color(0xFF808080),
    background = Color(0xFF121212), // 다크 배경
    surface = Color(0xFF1E1E1E), // 다크 서페이스
    onPrimary = Color.Black, // 프라이머리 위의 텍스트는 검정
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0), // 배경 위의 텍스트는 밝은 회색
    onSurface = Color(0xFFE0E0E0), // 서페이스 위의 텍스트는 밝은 회색
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFD0D0D0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF90CBFB),
    secondary = Color(0xFFCCCCCC),
    tertiary = Color(0xFFAAAAAA),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun SlideITTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // 커스텀 다크 모드를 위해 비활성화
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}