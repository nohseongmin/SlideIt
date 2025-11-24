package com.example.slideit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.data.model.CanvasCardData
import com.example.slideit.data.model.CardElement
import com.example.slideit.data.model.CanvasElement
import com.example.slideit.data.model.ElementType


/**
 * 통합 명함 렌더러
 */
@Composable
fun CardRenderer(
    card: BusinessCard,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 340.dp,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    translationX: Float = 0f,
    translationY: Float = 0f,
    glareX: Float = 50f,
    glareY: Float = 50f,
    glareOpacity: Float = 0f,
    showRotated: Boolean = false
) {
    when (card.editorType) {
        "SIMPLE" -> {
            SimpleCardRenderer(
                card = card,
                modifier = modifier,
                cardWidth = cardWidth,
                rotationX = rotationX,
                rotationY = rotationY,
                translationX = translationX,
                translationY = translationY,
                glareX = glareX,
                glareY = glareY,
                glareOpacity = glareOpacity,
                showRotated = showRotated
            )
        }
        "CANVAS" -> {
            CanvasCardRenderer(
                card = card,
                modifier = modifier,
                cardWidth = cardWidth,
                rotationX = rotationX,
                rotationY = rotationY,
                glareX = glareX,
                glareY = glareY,
                glareOpacity = glareOpacity,
                showRotated = showRotated
            )
        }
        else -> {
            SimpleCardRenderer(
                card = card,
                modifier = modifier,
                cardWidth = cardWidth,
                rotationX = rotationX,
                rotationY = rotationY,
                translationX = translationX,
                translationY = translationY,
                glareX = glareX,
                glareY = glareY,
                glareOpacity = glareOpacity,
                showRotated = showRotated
            )
        }
    }
}

/**
 * 텍스트 기반 명함 렌더러
 */
@Composable
fun SimpleCardRenderer(
    card: BusinessCard,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 340.dp,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    translationX: Float = 0f,
    translationY: Float = 0f,
    glareX: Float = 50f,
    glareY: Float = 50f,
    glareOpacity: Float = 0f,
    showRotated: Boolean = false
) {
    Box(
        modifier = modifier
            .width(cardWidth)
            .aspectRatio(0.63f)
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                cameraDistance = 12f * density
            }
    ) {
        // 명함 베이스
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(card.backgroundColor)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 홀로그래픽 레인보우 효과
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = glareOpacity }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color(0xFFE0F7FA).copy(alpha = 0.3f), // Light Cyan
                                    Color(0xFFF3E5F5).copy(alpha = 0.2f), // Light Purple
                                    Color(0xFFFFF9C4).copy(alpha = 0.2f), // Light Yellow
                                    Color.Transparent
                                ),
                                center = Offset(glareX / 100f * 400, glareY / 100f * 600), // Adjust multiplier for effect
                                radius = 350f
                            )
                        )
                )

                // 명함 내용
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            this.translationX = translationX
                            this.translationY = translationY
                        }
                ) {
                    if (showRotated) {
                        // 90도 회전된 레이아웃 (명함 공유 화면용)
                        RotatedCardContent(card)
                    } else {
                        // 일반 레이아웃 (명함 보관함용)
                        NormalCardContent(card)
                    }
                }
            }
        }
    }
}

/**
 * 90도 회전된 명함 내용 (명함 공유 화면용)
 */
@Composable
private fun RotatedCardContent(card: BusinessCard) {
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
            if (card.position.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.position,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(card.textColor).copy(alpha = 0.8f)
                )
            }
            if (card.department.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.department,
                    fontSize = 16.sp,
                    color = Color(card.textColor).copy(alpha = 0.7f)
                )
            }
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

/**
 * 일반 명함 내용 (명함 보관함용)
 */
@Composable
private fun NormalCardContent(card: BusinessCard) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단 - 이름 및 직책
        Column {
            Text(
                text = card.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(card.textColor)
            )
            if (card.position.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.position,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(card.textColor).copy(alpha = 0.8f)
                )
            }
            if (card.department.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = card.department,
                    fontSize = 12.sp,
                    color = Color(card.textColor).copy(alpha = 0.7f)
                )
            }
        }

        // 하단 - 연락처 및 회사명
        Column {
            Text(
                text = card.company,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(card.textColor).copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = card.phone,
                fontSize = 11.sp,
                color = Color(card.textColor).copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = card.email,
                fontSize = 11.sp,
                color = Color(card.textColor).copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Canvas 기반 명함 렌더러
 */
@Composable
fun CanvasCardRenderer(
    card: BusinessCard,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 340.dp,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    glareX: Float = 50f,
    glareY: Float = 50f,
    glareOpacity: Float = 0f,
    showRotated: Boolean = false
) {
    val canvasData = card.canvasData?.let { CanvasCardData.fromJson(it) }

    if (canvasData == null) {
        // 데이터가 없거나 파싱 실패 시 Simple 렌더러로 대체
        SimpleCardRenderer(
            card = card,
            modifier = modifier,
            cardWidth = cardWidth,
            rotationX = rotationX,
            rotationY = rotationY,
            glareX = glareX,
            glareY = glareY,
            glareOpacity = glareOpacity,
            showRotated = showRotated
        )
        return
    }

    Box(
        modifier = modifier
            .width(cardWidth)
            .aspectRatio(if (showRotated) canvasData.height / canvasData.width else canvasData.width / canvasData.height)
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                if(showRotated) {
                    this.rotationZ = 90f
                }
                cameraDistance = 12f * density
                clip = false
            }
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(canvasData.backgroundColor)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 홀로그래픽 레인보우 효과
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = glareOpacity }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color(0xFFE0F7FA).copy(alpha = 0.3f), // Light Cyan
                                    Color(0xFFF3E5F5).copy(alpha = 0.2f), // Light Purple
                                    Color(0xFFFFF9C4).copy(alpha = 0.2f), // Light Yellow
                                    Color.Transparent
                                ),
                                center = Offset(glareX / 100f * canvasData.width, glareY / 100f * canvasData.height),
                                radius = canvasData.width / 2f
                            )
                        )
                )

                // 캔버스 요소 렌더링
                canvasData.elements.forEach { cardElement ->
                    val canvasElement = when (cardElement) {
                        is CardElement.TextElement -> CanvasElement(id = cardElement.id, type = ElementType.TEXT, position = Offset(cardElement.x, cardElement.y), size = Offset(cardElement.width, cardElement.height), rotation = cardElement.rotation, text = cardElement.text, fontSize = cardElement.fontSize, color = cardElement.color)
                        is CardElement.ImageElement -> CanvasElement(id = cardElement.id, type = ElementType.IMAGE, position = Offset(cardElement.x, cardElement.y), size = Offset(cardElement.width, cardElement.height), rotation = cardElement.rotation, imageUri = cardElement.imageUri)
                        is CardElement.ShapeElement -> CanvasElement(id = cardElement.id, type = ElementType.SHAPE, position = Offset(cardElement.x, cardElement.y), size = Offset(cardElement.width, cardElement.height), rotation = cardElement.rotation, shapeType = cardElement.shapeType, color = cardElement.fillColor)
                    }
                    CanvasElementView(
                        element = canvasElement,
                        isSelected = false,
                        onSelect = {},
                        onMove = {}
                    )
                }
            }
        }
    }
}
