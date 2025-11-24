package com.example.slideit.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import java.util.UUID

/**
 * 캔버스 편집기에서 사용하는 명함 요소들
 */
sealed class CardElement {
    abstract val id: String
    abstract val x: Float
    abstract val y: Float
    abstract val width: Float
    abstract val height: Float
    abstract val rotation: Float
    abstract val zIndex: Int

    /**
     * 텍스트 요소
     */
    data class TextElement(
        override val id: String = UUID.randomUUID().toString(),
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val rotation: Float = 0f,
        override val zIndex: Int = 0,
        val text: String,
        val fontSize: Float,
        val color: Long, // ARGB format
        val fontWeight: String = "Normal", // "Normal", "Bold", "Light"
        val textAlign: String = "Left" // "Left", "Center", "Right"
    ) : CardElement()

    /**
     * 이미지 요소
     */
    data class ImageElement(
        override val id: String = UUID.randomUUID().toString(),
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val rotation: Float = 0f,
        override val zIndex: Int = 0,
        val imageUri: String,
        val cornerRadius: Float = 0f,
        val alpha: Float = 1f
    ) : CardElement()

    /**
     * 도형 요소
     */
    data class ShapeElement(
        override val id: String = UUID.randomUUID().toString(),
        override val x: Float,
        override val y: Float,
        override val width: Float,
        override val height: Float,
        override val rotation: Float = 0f,
        override val zIndex: Int = 0,
        val shapeType: ShapeType,
        val fillColor: Long, // ARGB format
        val strokeColor: Long? = null,
        val strokeWidth: Float = 0f,
        val cornerRadius: Float = 0f
    ) : CardElement()
}

/**
 * 도형 타입
 */
enum class ShapeType {
    RECTANGLE,
    CIRCLE,
    ROUNDED_RECTANGLE
}

/**
 * 캔버스 명함 데이터
 */
data class CanvasCardData(
    val elements: List<CardElement> = emptyList(),
    val backgroundColor: Long = 0xFFFFFFFF,
    val width: Float = 1080f,
    val height: Float = 680f
) {
    /**
     * JSON으로 직렬화
     */
    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        /**
         * JSON에서 역직렬화
         */
        fun fromJson(json: String): CanvasCardData? {
            return try {
                Gson().fromJson(json, CanvasCardData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * 편집기 타입
 */
enum class EditorType {
    SIMPLE,  // 간편 편집 (텍스트 폼)
    CANVAS   // 상세 편집 (이미지 편집기)
}
