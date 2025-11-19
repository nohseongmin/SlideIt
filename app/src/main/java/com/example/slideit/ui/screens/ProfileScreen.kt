package com.example.slideit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.viewmodel.CardViewModel

/**
 * 개인 설정 화면
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = viewModel(),
    onNavigateToEditor: (Boolean) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val myCard by viewModel.firstMyCard.collectAsStateWithLifecycle(initialValue = null)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 프로필 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF90CBFB),
                            Color(0xFF6AB5F5)
                        )
                    )
                )
                .padding(top = 40.dp, bottom = 60.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 이미지
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "홍",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF90CBFB)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = myCard?.name ?: "내 명함 만들기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = myCard?.email ?: "명함을 만들어 공유하세요",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // 메뉴 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-40).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // 내 명함 관리
                if (myCard != null) {
                    MenuItem(
                        icon = Icons.Default.Edit,
                        title = "내 명함 수정",
                        subtitle = "내 명함 정보 수정",
                        onClick = { onNavigateToEditor(true) }
                    )
                } else {
                    MenuItem(
                        icon = Icons.Default.Add,
                        title = "내 명함 만들기",
                        subtitle = "나만의 명함 생성",
                        onClick = { onNavigateToEditor(true) }
                    )
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                // 개인 설정
                MenuItem(
                    icon = Icons.Default.Settings,
                    title = "개인 설정",
                    subtitle = "테마, 알림 등 설정",
                    onClick = onNavigateToSettings
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 버전 정보
        Text(
            text = "Slide It! v1.0.0",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 배경
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF90CBFB).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF90CBFB),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // 화살표
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}
