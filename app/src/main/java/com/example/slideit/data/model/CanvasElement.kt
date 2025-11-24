package com.example.slideit.data.model

import androidx.compose.ui.geometry.Offset

/**
 * 캔버스 요소 데이터 클래스
 */
data class CanvasElement(
    val id: String,
    val type: ElementType,
    val position: Offset,
    val size: Offset,
    val rotation: Float,
    val text: String? = null,
    val fontSize: Float? = null,
    val color: Long? = null,
    val imageUri: String? = null,
    val shapeType: ShapeType? = null
)

/**
 * 요소 타입
 */
enum class ElementType {
    TEXT,
    IMAGE,
    SHAPE
}
