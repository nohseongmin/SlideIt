package com.example.slideit.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import com.example.slideit.ui.components.CardRenderer
import com.example.slideit.util.ShareUtil
import com.example.slideit.util.VCardUtil
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    val targetRotationXState = remember { mutableFloatStateOf(0f) }
    val targetRotationYState = remember { mutableFloatStateOf(0f) }
    val targetGlareXState = remember { mutableFloatStateOf(50f) }
    val targetGlareYState = remember { mutableFloatStateOf(50f) }
    val glareOpacityState = remember { mutableFloatStateOf(0f) }
    val targetTranslationXState = remember { mutableFloatStateOf(0f) }
    val targetTranslationYState = remember { mutableFloatStateOf(0f) }
    val sendAnimationY = remember { Animatable(0f) }

    // 스프링 애니메이션으로 부드럽게 전환
    val rotationX by animateFloatAsState(
        targetValue = targetRotationXState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationX"
    )

    val rotationY by animateFloatAsState(
        targetValue = targetRotationYState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationY"
    )

    val glareX by animateFloatAsState(
        targetValue = targetGlareXState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "glareX"
    )

    val glareY by animateFloatAsState(
        targetValue = targetGlareYState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "glareY"
    )

    val opacity by animateFloatAsState(
        targetValue = glareOpacityState.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "opacity"
    )

    val translationX by animateFloatAsState(
        targetValue = targetTranslationXState.value,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "translationX"
    )

    val translationY by animateFloatAsState(
        targetValue = targetTranslationYState.value,
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
                                .graphicsLayer {
                                    this.translationY = translationY + sendAnimationY.value
                                }
                                .shadow(
                                    elevation = 24.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color(0x40000000),
                                    spotColor = Color(0x40000000),
                                    clip = false
                                )
                                .pointerInput(Unit) {
                                    var totalDrag = Offset.Zero
                                    detectDragGestures(
                                        onDragStart = {
                                            totalDrag = Offset.Zero
                                            glareOpacityState.value = 1f
                                        },
                                        onDragEnd = {
                                            if (totalDrag.y < -200) { // Swipe up
                                                myCard?.let { cardToShare ->
                                                    coroutineScope.launch {
                                                        try {
                                                            val vCardString = VCardUtil.createVCardString(cardToShare)
                                                            val vcfFile = File(context.cacheDir, "business_card.vcf")
                                                            FileOutputStream(vcfFile).use {
                                                                it.write(vCardString.toByteArray())
                                                            }

                                                            val shareIntent = ShareUtil.createShareIntent(context, vcfFile, "text/vcard")
                                                            context.startActivity(shareIntent)
                                                            
                                                            sendAnimationY.animateTo(
                                                                targetValue = -2000f,
                                                                animationSpec = tween(durationMillis = 500, easing = FastOutLinearInEasing)
                                                            )
                                                            sendAnimationY.snapTo(0f)


                                                        } catch (e: IOException) {
                                                            e.printStackTrace()
                                                            Toast.makeText(context, "Error creating share file", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            }
                                            targetRotationXState.value = 0f
                                            targetRotationYState.value = 0f
                                            targetTranslationXState.value = 0f
                                            targetTranslationYState.value = 0f
                                            glareOpacityState.value = 0f
                                        },
                                        onDragCancel = {
                                            totalDrag = Offset.Zero
                                            targetRotationXState.value = 0f
                                            targetRotationYState.value = 0f
                                            targetTranslationXState.value = 0f
                                            targetTranslationYState.value = 0f
                                            glareOpacityState.value = 0f
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        totalDrag = totalDrag.plus(dragAmount)

                                        val centerX = size.width / 2f
                                        val centerY = size.height / 2f
                                        val x = change.position.x
                                        val y = change.position.y

                                        targetRotationXState.value = ((y - centerY) / centerY) * -25f
                                        targetRotationYState.value = ((x - centerX) / centerX) * 25f
                                        targetGlareXState.value = (x / size.width) * 100f
                                        targetGlareYState.value = (y / size.height) * 100f
                                        targetTranslationXState.value = ((x - centerX) / centerX) * 10f
                                        targetTranslationYState.value = ((y - centerY) / centerY) * 10f
                                    }
                                },
                            cardWidth = 340.dp,
                            rotationX = rotationX,
                            rotationY = rotationY,
                            translationX = translationX,
                            glareX = glareX,
                            glareY = glareY,
                            glareOpacity = opacity,
                            showRotated = true
                        )
                    }
                }    }
}
