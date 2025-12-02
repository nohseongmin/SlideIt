package com.example.slideit.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
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
    val width: Float = 1080f,  // Landscape width
    val height: Float = 680f   // Landscape height
) {
    /**
     * JSON으로 직렬화 (수동 직렬화로 sealed class 타입 정보 보존)
     */
    fun toJson(): String {
        return try {
            val jsonObject = JSONObject()
            jsonObject.put("backgroundColor", backgroundColor)
            jsonObject.put("width", width)
            jsonObject.put("height", height)

            val elementsArray = JSONArray()
            elements.forEach { element ->
                val elementObj = JSONObject()
                elementObj.put("id", element.id)
                elementObj.put("x", element.x)
                elementObj.put("y", element.y)
                elementObj.put("width", element.width)
                elementObj.put("height", element.height)
                elementObj.put("rotation", element.rotation)
                elementObj.put("zIndex", element.zIndex)

                when (element) {
                    is CardElement.TextElement -> {
                        elementObj.put("type", "TEXT")
                        elementObj.put("text", element.text)
                        elementObj.put("fontSize", element.fontSize)
                        elementObj.put("color", element.color)
                        elementObj.put("fontWeight", element.fontWeight)
                        elementObj.put("textAlign", element.textAlign)
                    }
                    is CardElement.ImageElement -> {
                        elementObj.put("type", "IMAGE")
                        elementObj.put("imageUri", element.imageUri)
                        elementObj.put("cornerRadius", element.cornerRadius)
                        elementObj.put("alpha", element.alpha)
                    }
                    is CardElement.ShapeElement -> {
                        elementObj.put("type", "SHAPE")
                        elementObj.put("shapeType", element.shapeType.name)
                        elementObj.put("fillColor", element.fillColor)
                        elementObj.put("strokeColor", element.strokeColor ?: 0L)
                        elementObj.put("strokeWidth", element.strokeWidth)
                        elementObj.put("cornerRadius", element.cornerRadius)
                    }
                }
                elementsArray.put(elementObj)
            }
            jsonObject.put("elements", elementsArray)

            jsonObject.toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    companion object {
        /**
         * JSON에서 역직렬화 (수동 역직렬화로 sealed class 타입 정보 복원)
         */
        fun fromJson(json: String): CanvasCardData? {
            return try {
                val jsonObject = JSONObject(json)
                val backgroundColor = jsonObject.optLong("backgroundColor", 0xFFFFFFFF)
                val width = jsonObject.optDouble("width", 1080.0).toFloat()
                val height = jsonObject.optDouble("height", 680.0).toFloat()

                val elements = mutableListOf<CardElement>()
                val elementsArray = jsonObject.optJSONArray("elements")

                elementsArray?.let {
                    for (i in 0 until it.length()) {
                        val elementObj = it.getJSONObject(i)
                        val type = elementObj.optString("type", "")

                        val id = elementObj.optString("id", UUID.randomUUID().toString())
                        val x = elementObj.optDouble("x", 0.0).toFloat()
                        val y = elementObj.optDouble("y", 0.0).toFloat()
                        val width = elementObj.optDouble("width", 0.0).toFloat()
                        val height = elementObj.optDouble("height", 0.0).toFloat()
                        val rotation = elementObj.optDouble("rotation", 0.0).toFloat()
                        val zIndex = elementObj.optInt("zIndex", 0)

                        val element = when (type) {
                            "TEXT" -> CardElement.TextElement(
                                id = id,
                                x = x,
                                y = y,
                                width = width,
                                height = height,
                                rotation = rotation,
                                zIndex = zIndex,
                                text = elementObj.optString("text", ""),
                                fontSize = elementObj.optDouble("fontSize", 16.0).toFloat(),
                                color = elementObj.optLong("color", 0xFF000000),
                                fontWeight = elementObj.optString("fontWeight", "Normal"),
                                textAlign = elementObj.optString("textAlign", "Left")
                            )
                            "IMAGE" -> CardElement.ImageElement(
                                id = id,
                                x = x,
                                y = y,
                                width = width,
                                height = height,
                                rotation = rotation,
                                zIndex = zIndex,
                                imageUri = elementObj.optString("imageUri", ""),
                                cornerRadius = elementObj.optDouble("cornerRadius", 0.0).toFloat(),
                                alpha = elementObj.optDouble("alpha", 1.0).toFloat()
                            )
                            "SHAPE" -> {
                                val shapeTypeName = elementObj.optString("shapeType", "RECTANGLE")
                                val shapeType = try {
                                    ShapeType.valueOf(shapeTypeName)
                                } catch (e: Exception) {
                                    ShapeType.RECTANGLE
                                }

                                CardElement.ShapeElement(
                                    id = id,
                                    x = x,
                                    y = y,
                                    width = width,
                                    height = height,
                                    rotation = rotation,
                                    zIndex = zIndex,
                                    shapeType = shapeType,
                                    fillColor = elementObj.optLong("fillColor", 0xFF90CBFB),
                                    strokeColor = elementObj.optLong("strokeColor", 0L).takeIf { it != 0L },
                                    strokeWidth = elementObj.optDouble("strokeWidth", 0.0).toFloat(),
                                    cornerRadius = elementObj.optDouble("cornerRadius", 0.0).toFloat()
                                )
                            }
                            else -> null
                        }

                        element?.let { elements.add(it) }
                    }
                }

                CanvasCardData(
                    elements = elements,
                    backgroundColor = backgroundColor,
                    width = width,
                    height = height
                )
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
