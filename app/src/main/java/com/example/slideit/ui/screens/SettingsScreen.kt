package com.example.slideit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 개인 설정 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "개인 설정",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF90CBFB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F9F9),
                            Color(0xFFEFEFEF)
                        )
                    )
                )
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 계정 섹션
            Text(
                text = "계정",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.Person,
                        title = "프로필 편집",
                        subtitle = "이름, 이메일 등 변경",
                        onClick = { /* TODO: 프로필 편집 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Lock,
                        title = "비밀번호 변경",
                        subtitle = "보안을 위해 정기적으로 변경하세요",
                        onClick = { /* TODO: 비밀번호 변경 화면으로 이동 */ }
                    )
                }
            }

            // 앱 설정 섹션
            Text(
                text = "앱 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "알림",
                        subtitle = "명함 관련 알림 수신",
                        checked = isNotificationsEnabled,
                        onCheckedChange = { isNotificationsEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = "다크 모드",
                        subtitle = "어두운 테마 사용",
                        checked = isDarkModeEnabled,
                        onCheckedChange = { isDarkModeEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Settings,
                        title = "언어",
                        subtitle = "한국어",
                        onClick = { /* TODO: 언어 설정 화면으로 이동 */ }
                    )
                }
            }

            // 기타 섹션
            Text(
                text = "기타",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "앱 정보",
                        subtitle = "버전 1.0.0",
                        onClick = { /* TODO: 앱 정보 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "도움말",
                        subtitle = "사용 가이드 및 FAQ",
                        onClick = { /* TODO: 도움말 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "이용약관",
                        subtitle = "서비스 이용약관 및 개인정보 처리방침",
                        onClick = { /* TODO: 이용약관 화면으로 이동 */ }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 로그아웃 버튼
            Button(
                onClick = { /* TODO: 로그아웃 처리 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "로그아웃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 배경
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF90CBFB).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF90CBFB),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 화살표
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 배경
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF90CBFB).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF90CBFB),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 스위치
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF90CBFB),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}
