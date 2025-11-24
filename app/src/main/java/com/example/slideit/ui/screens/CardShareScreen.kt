package com.example.slideit.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.ui.components.CardRenderer
import com.example.slideit.util.BitmapConverter
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch

/**
 * 명함 공유 화면 - 3D 홀로그래픽 효과
 */
@Composable
fun CardShareScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = viewModel(),
    onNavigateToEditor: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val myCard by viewModel.firstMyCard.collectAsStateWithLifecycle(initialValue = null)
    var targetRotationX by remember { mutableFloatStateOf(0f) }
    var targetRotationY by remember { mutableFloatStateOf(0f) }
    var targetGlareX by remember { mutableFloatStateOf(50f) }
    var targetGlareY by remember { mutableFloatStateOf(50f) }
    var glareOpacity by remember { mutableFloatStateOf(0f) }
    var targetTranslationX by remember { mutableFloatStateOf(0f) }
    var targetTranslationY by remember { mutableFloatStateOf(0f) }

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

    val translationX by animateFloatAsState(
        targetValue = targetTranslationX,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "translationX"
    )

    val translationY by animateFloatAsState(
        targetValue = targetTranslationY,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "translationY"
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
                modifier = Modifier
                    .padding(32.dp)
                    .clickable { onNavigateToEditor() }
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToEditor,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF90CBFB)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "내 명함 만들기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            // 내 명함 표시
            val card = myCard ?: return

            Box(modifier = Modifier.padding(32.dp)) {
                CardRenderer(
                    card = card,
                    modifier = Modifier
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0x40000000),
                            spotColor = Color(0x40000000),
                            clip = false
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    glareOpacity = 1f
                                },
                                onDragEnd = {
                                    targetRotationX = 0f
                                    targetRotationY = 0f
                                    targetTranslationX = 0f
                                    targetTranslationY = 0f
                                    glareOpacity = 0f
                                },
                                onDragCancel = {
                                    targetRotationX = 0f
                                    targetRotationY = 0f
                                    targetTranslationX = 0f
                                    targetTranslationY = 0f
                                    glareOpacity = 0f
                                }
                            ) { change, _ ->
                                change.consume()

                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val x = change.position.x
                                val y = change.position.y

                                targetRotationX = ((y - centerY) / centerY) * -25f
                                targetRotationY = ((x - centerX) / centerX) * 25f
                                targetGlareX = (x / size.width) * 100f
                                targetGlareY = (y / size.height) * 100f
                                targetTranslationX = ((x - centerX) / centerX) * 10f
                                targetTranslationY = ((y - centerY) / centerY) * 10f
                            }
                        },
                    cardWidth = 340.dp,
                    rotationX = rotationX,
                    rotationY = rotationY,
                    translationX = translationX,
                    translationY = translationY,
                    glareX = glareX,
                    glareY = glareY,
                    glareOpacity = opacity,
                    showRotated = true
                )
            }
        }
    }
}