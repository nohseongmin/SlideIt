package com.example.slideit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalDensity
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = glareOpacity }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color(0xFFE0F7FA).copy(alpha = 0.3f), 
                                    Color(0xFFF3E5F5).copy(alpha = 0.2f), 
                                    Color(0xFFFFF9C4).copy(alpha = 0.2f), 
                                    Color.Transparent
                                ),
                                center = Offset(glareX / 100f * 400, glareY / 100f * 600),
                                radius = 350f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            this.translationX = translationX
                            this.translationY = translationY
                        }
                ) {
                    if (showRotated) {
                        RotatedCardContent(card)
                    } else {
                        NormalCardContent(card)
                    }
                }
            }
        }
    }
}

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
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = card.name, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(card.textColor), letterSpacing = 0.5.sp)
            if (card.position.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = card.position, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(card.textColor).copy(alpha = 0.8f))
            }
            if (card.department.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = card.department, fontSize = 16.sp, color = Color(card.textColor).copy(alpha = 0.7f))
            }
        }

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

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = card.company, fontSize = 14.sp, color = Color(card.textColor).copy(alpha = 0.6f), letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "M", fontSize = 12.sp, color = Color(card.textColor).copy(alpha = 0.6f), fontWeight = FontWeight.Medium, modifier = Modifier.width(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = card.phone, fontSize = 13.sp, color = Color(card.textColor).copy(alpha = 0.9f))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "E", fontSize = 12.sp, color = Color(card.textColor).copy(alpha = 0.6f), fontWeight = FontWeight.Medium, modifier = Modifier.width(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = card.email, fontSize = 13.sp, color = Color(card.textColor).copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
private fun NormalCardContent(card: BusinessCard) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = card.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(card.textColor))
            if (card.position.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = card.position, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(card.textColor).copy(alpha = 0.8f))
            }
            if (card.department.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = card.department, fontSize = 12.sp, color = Color(card.textColor).copy(alpha = 0.7f))
            }
        }

        Column {
            Text(text = card.company, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(card.textColor).copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = card.phone, fontSize = 11.sp, color = Color(card.textColor).copy(alpha = 0.9f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = card.email, fontSize = 11.sp, color = Color(card.textColor).copy(alpha = 0.9f))
        }
    }
}

@Composable
fun CanvasCardRenderer(
    card: BusinessCard,
    modifier: Modifier = Modifier,
    cardWidth: Dp,
    rotationX: Float,
    rotationY: Float,
    glareX: Float,
    glareY: Float,
    glareOpacity: Float,
    showRotated: Boolean
) {
    val canvasData = card.canvasData?.let { CanvasCardData.fromJson(it) }

    if (canvasData == null) {
        SimpleCardRenderer(card = card, modifier = modifier, cardWidth = cardWidth, rotationX = rotationX, rotationY = rotationY, glareX = glareX, glareY = glareY, glareOpacity = glareOpacity, showRotated = showRotated)
        return
    }

    val cardRenderWidth = cardWidth
    val cardRenderHeight = (cardWidth.value * (680.0f / 1080.0f)).dp
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .width(cardRenderWidth)
            .height(cardRenderHeight)
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                if (showRotated) {
                    this.rotationZ = 90f
                }
                cameraDistance = 12f * density.density // Use density.density
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = glareOpacity }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color(0xFFE0F7FA).copy(alpha = 0.3f), Color(0xFFF3E5F5).copy(alpha = 0.2f), Color(0xFFFFF9C4).copy(alpha = 0.2f), Color.Transparent),
                                center = Offset(glareX / 100f * canvasData.width, glareY / 100f * canvasData.height),
                                radius = canvasData.width / 2f
                            )
                        )
                )

                canvasData.elements.forEach { cardElement ->
                    // Calculate element dimensions in Dp
                    val elementXOffsetDp = cardRenderWidth * cardElement.x
                    val elementYOffsetDp = cardRenderHeight * cardElement.y
                    val elementWidthDp = cardRenderWidth * cardElement.width
                    val elementHeightDp = cardRenderHeight * cardElement.height

                    // Scale fontSize based on the card's overall scale factor
                    val scaleFactor = cardRenderWidth.value / canvasData.width
                    val fontSize = if (cardElement is CardElement.TextElement) {
                        cardElement.fontSize * scaleFactor
                    } else {
                        16f // Default font size for non-text elements (should not be used)
                    }

                    val canvasElement = when (cardElement) {
                        is CardElement.TextElement -> CanvasElement(id = cardElement.id, type = ElementType.TEXT, position = Offset(elementXOffsetDp.value, elementYOffsetDp.value), size = Offset(elementWidthDp.value, elementHeightDp.value), rotation = cardElement.rotation, text = cardElement.text, fontSize = fontSize, color = cardElement.color)
                        is CardElement.ImageElement -> CanvasElement(id = cardElement.id, type = ElementType.IMAGE, position = Offset(elementXOffsetDp.value, elementYOffsetDp.value), size = Offset(elementWidthDp.value, elementHeightDp.value), rotation = cardElement.rotation, imageUri = cardElement.imageUri)
                        is CardElement.ShapeElement -> CanvasElement(id = cardElement.id, type = ElementType.SHAPE, position = Offset(elementXOffsetDp.value, elementYOffsetDp.value), size = Offset(elementWidthDp.value, elementHeightDp.value), rotation = cardElement.rotation, shapeType = cardElement.shapeType, color = cardElement.fillColor)
                    }
                    CanvasElementView(
                        element = canvasElement,
                        isSelected = false,
                        onSelect = {},
                        onMove = {},
                        onResize = {},
                        onRotate = {},
                        onRotateBy = {}
                    )
                }
            }
        }
    }
}