package com.example.slideit.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.viewmodel.CardViewModel

/**
 * 명함 공유 화면 - 3D 홀로그래픽 효과
 */
@Composable
fun CardShareScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = viewModel()
) {
    val myCard by viewModel.firstMyCard.collectAsStateWithLifecycle(initialValue = null)
    var targetRotationX by remember { mutableStateOf(0f) }
    var targetRotationY by remember { mutableStateOf(0f) }
    var targetGlareX by remember { mutableStateOf(50f) }
    var targetGlareY by remember { mutableStateOf(50f) }
    var glareOpacity by remember { mutableStateOf(0f) }

    // 스프링 애니메이션으로 부드럽게 전환
    val rotationX by animateFloatAsState(
        targetValue = targetRotationX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = targetRotationY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationY"
    )

    val glareX by animateFloatAsState(
        targetValue = targetGlareX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "glareX"
    )

    val glareY by animateFloatAsState(
        targetValue = targetGlareY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "glareY"
    )

    val opacity by animateFloatAsState(
        targetValue = glareOpacity,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "opacity"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF90CBFB),
                        Color(0xFF6AB5F5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (myCard == null) {
            // 내 명함이 없을 때
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "아직 명함이 없습니다",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "프로필 화면에서 내 명함을 만들어주세요",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        } else {
            // 내 명함 표시
            val card = myCard ?: return

            Box(
                modifier = Modifier
                    .width(340.dp)
                    .aspectRatio(0.63f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                glareOpacity = 1f
                            },
                            onDragEnd = {
                                targetRotationX = 0f
                                targetRotationY = 0f
                                glareOpacity = 0f
                            },
                            onDragCancel = {
                                targetRotationX = 0f
                                targetRotationY = 0f
                                glareOpacity = 0f
                            }
                        ) { change, _ ->
                            change.consume()

                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val x = change.position.x
                            val y = change.position.y

                            targetRotationX = ((y - centerY) / centerY) * -15f
                            targetRotationY = ((x - centerX) / centerX) * 15f
                            targetGlareX = (x / size.width) * 100f
                            targetGlareY = (y / size.height) * 100f
                        }
                    }
                    .graphicsLayer {
                        this.rotationX = rotationX
                        this.rotationY = rotationY
                        cameraDistance = 12f * density
                    }
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x40000000),
                        spotColor = Color(0x40000000)
                    )
            ) {
                // 명함 베이스
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(card.backgroundColor)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 홀로그래픽 레인보우 효과
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = opacity }
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0x1AFF0080),
                                            Color(0x1AFF8C00),
                                            Color(0x1A40E0D0),
                                            Color(0x1A90CBFB),
                                            Color(0x1AC471ED)
                                        ),
                                        start = Offset(glareX * 10, glareY * 10),
                                        end = Offset(1000f - glareX * 10, 1000f - glareY * 10)
                                    )
                                )
                        )

                        // 명함 내용 - 90도 회전된 레이아웃
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .graphicsLayer {
                                    rotationZ = 90f
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 좌측 - 이름 및 직책
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = card.name,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(card.textColor),
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = card.position,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(card.textColor).copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = card.department,
                                    fontSize = 16.sp,
                                    color = Color(card.textColor).copy(alpha = 0.7f)
                                )
                            }

                            // 중앙 구분선
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(80.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(card.textColor).copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // 우측 - 연락처 및 회사명
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = card.company,
                                    fontSize = 14.sp,
                                    color = Color(card.textColor).copy(alpha = 0.6f),
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "M",
                                        fontSize = 12.sp,
                                        color = Color(card.textColor).copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.width(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = card.phone,
                                        fontSize = 13.sp,
                                        color = Color(card.textColor).copy(alpha = 0.9f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "E",
                                        fontSize = 12.sp,
                                        color = Color(card.textColor).copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.width(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = card.email,
                                        fontSize = 13.sp,
                                        color = Color(card.textColor).copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
