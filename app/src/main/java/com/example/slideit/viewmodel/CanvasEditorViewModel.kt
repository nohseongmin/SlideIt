package com.example.slideit.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import com.example.slideit.data.model.CardElement
import com.example.slideit.data.model.CanvasElement
import com.example.slideit.data.model.ElementType
import com.example.slideit.data.model.ShapeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 캔버스 에디터 ViewModel
 */
class CanvasEditorViewModel(application: Application) : AndroidViewModel(application) {

    // 캔버스에 추가된 요소들
    private val _elements = mutableStateListOf<CanvasElement>()
    val elements: SnapshotStateList<CanvasElement> = _elements

    // 선택된 요소 ID
    private val _selectedElementId = MutableStateFlow<String?>(null)
    val selectedElementId: StateFlow<String?> = _selectedElementId.asStateFlow()

    // 배경 색상
    private val _backgroundColor = MutableStateFlow(0xFFFFFFFF)
    val backgroundColor: StateFlow<Long> = _backgroundColor.asStateFlow()


    /**
     * 텍스트 요소 추가
     */
    fun addTextElement(
        text: String,
        fontSize: Float = 16f,
        color: Long = 0xFF000000,
        position: Offset = Offset(100f, 100f)
    ) {
        val element = CanvasElement(
            id = generateId(),
            type = ElementType.TEXT,
            position = position,
            size = Offset(200f, 50f),
            rotation = 0f,
            text = text,
            fontSize = fontSize,
            color = color
        )
        _elements.add(element)
    }

    /**
     * 이미지 요소 추가
     */
    fun addImageElement(
        imageUri: String,
        position: Offset = Offset(100f, 100f),
        size: Offset = Offset(200f, 200f)
    ) {
        val element = CanvasElement(
            id = generateId(),
            type = ElementType.IMAGE,
            position = position,
            size = size,
            rotation = 0f,
            imageUri = imageUri
        )
        _elements.add(element)
    }

    /**
     * 도형 요소 추가
     */
    fun addShapeElement(
        shapeType: ShapeType,
        position: Offset = Offset(100f, 100f),
        size: Offset = Offset(100f, 100f),
        color: Long = 0xFF90CBFB
    ) {
        val element = CanvasElement(
            id = generateId(),
            type = ElementType.SHAPE,
            position = position,
            size = size,
            rotation = 0f,
            shapeType = shapeType,
            color = color
        )
        _elements.add(element)
    }

    /**
     * 요소 선택
     */
    fun selectElement(id: String) {
        _selectedElementId.value = id
    }

    /**
     * 선택 해제
     */
    fun deselectElement() {
        _selectedElementId.value = null
    }

    /**
     * 요소 이동
     */
    fun moveElement(id: String, offset: Offset) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            _elements[index] = _elements[index].copy(
                position = _elements[index].position + offset
            )
        }
    }

    /**
     * 요소 크기 조정
     */
    fun resizeElement(id: String, dragAmount: Offset) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            val element = _elements[index]
            val currentSize = element.size
            val newSize = Offset(
                (currentSize.x + dragAmount.x).coerceAtLeast(20f),
                (currentSize.y + dragAmount.y).coerceAtLeast(20f)
            )

            var updatedElement = element.copy(size = newSize)

            if (element.type == ElementType.TEXT && element.fontSize != null && currentSize.x > 0) {
                val widthRatio = newSize.x / currentSize.x
                val newFontSize = element.fontSize * widthRatio
                updatedElement = updatedElement.copy(fontSize = newFontSize.coerceAtLeast(8f))
            }

            _elements[index] = updatedElement
        }
    }

    /**
     * 요소 회전
     */
    fun rotateElement(id: String, rotation: Float) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            _elements[index] = _elements[index].copy(rotation = rotation)
        }
    }
    
    fun rotateElementBy(id: String, delta: Float) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            _elements[index] = _elements[index].copy(rotation = _elements[index].rotation + delta)
        }
    }

    /**
     * 요소 삭제
     */
    fun deleteElement(id: String) {
        _elements.removeAll { it.id == id }
        if (_selectedElementId.value == id) {
            _selectedElementId.value = null
        }
    }

    /**
     * 선택된 요소 삭제
     */
    fun deleteSelectedElement() {
        _selectedElementId.value?.let { id ->
            deleteElement(id)
        }
    }

    /**
     * 요소를 앞으로 이동
     */
    fun bringToFront(id: String) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1 && index < _elements.size - 1) {
            val element = _elements.removeAt(index)
            _elements.add(element)
        }
    }

    /**
     * 요소를 뒤로 이동
     */
    fun sendToBack(id: String) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index > 0) {
            val element = _elements.removeAt(index)
            _elements.add(0, element)
        }
    }

    /**
     * 배경 색상 설정
     */
    fun setBackgroundColor(color: Long) {
        _backgroundColor.value = color
    }

    /**
     * 텍스트 요소 업데이트
     */
    fun updateTextElement(id: String, text: String, fontSize: Float, color: Long) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            _elements[index] = _elements[index].copy(
                text = text,
                fontSize = fontSize,
                color = color
            )
        }
    }

    /**
     * 도형 색상 업데이트
     */
    fun updateShapeColor(id: String, color: Long) {
        val index = _elements.indexOfFirst { it.id == id }
        if (index != -1) {
            _elements[index] = _elements[index].copy(color = color)
        }
    }

    /**
     * 캔버스 데이터를 CardElement 리스트로 변환 (편집기 -> 저장)
     */
    fun toCardElements(): List<CardElement> {
        // Editor is portrait (680w x 1080h)
        // Saved data is portrait (680w x 1080h)
        // No coordinate transformation needed, just relative conversion.
        val cardWidth = 680f
        val cardHeight = 1080f

        return elements.map { canvasElement ->
            val relativeX = canvasElement.position.x / cardWidth
            val relativeY = canvasElement.position.y / cardHeight
            val relativeWidth = canvasElement.size.x / cardWidth
            val relativeHeight = canvasElement.size.y / cardHeight

            when (canvasElement.type) {
                ElementType.TEXT -> CardElement.TextElement(id = canvasElement.id, x = relativeX, y = relativeY, width = relativeWidth, height = relativeHeight, rotation = canvasElement.rotation, text = canvasElement.text ?: "", fontSize = canvasElement.fontSize ?: 16f, color = canvasElement.color ?: 0xFF000000)
                ElementType.IMAGE -> CardElement.ImageElement(id = canvasElement.id, x = relativeX, y = relativeY, width = relativeWidth, height = relativeHeight, rotation = canvasElement.rotation, imageUri = canvasElement.imageUri ?: "")
                ElementType.SHAPE -> CardElement.ShapeElement(id = canvasElement.id, x = relativeX, y = relativeY, width = relativeWidth, height = relativeHeight, rotation = canvasElement.rotation, zIndex = 0, shapeType = canvasElement.shapeType ?: com.example.slideit.data.model.ShapeType.RECTANGLE, fillColor = canvasElement.color ?: 0xFF90CBFB)
            }
        }
    }

    /**
     * CardElement 리스트에서 캔버스 데이터 복원 (저장 -> 편집기)
     */
    fun fromCardElements(cardElements: List<CardElement>) {
        _elements.clear()
        val cardWidth = 680f
        val cardHeight = 1080f

        cardElements.forEach { cardElement ->
            val absoluteX = cardElement.x * cardWidth
            val absoluteY = cardElement.y * cardHeight
            val absoluteWidth = cardElement.width * cardWidth
            val absoluteHeight = cardElement.height * cardHeight

            when (cardElement) {
                is CardElement.TextElement -> _elements.add(CanvasElement(id = cardElement.id, type = ElementType.TEXT, position = Offset(absoluteX, absoluteY), size = Offset(absoluteWidth, absoluteHeight), rotation = cardElement.rotation, text = cardElement.text, fontSize = cardElement.fontSize, color = cardElement.color))
                is CardElement.ImageElement -> _elements.add(CanvasElement(id = cardElement.id, type = ElementType.IMAGE, position = Offset(absoluteX, absoluteY), size = Offset(absoluteWidth, absoluteHeight), rotation = cardElement.rotation, imageUri = cardElement.imageUri))
                is CardElement.ShapeElement -> _elements.add(CanvasElement(id = cardElement.id, type = ElementType.SHAPE, position = Offset(absoluteX, absoluteY), size = Offset(absoluteWidth, absoluteHeight), rotation = cardElement.rotation, shapeType = cardElement.shapeType, color = cardElement.fillColor))
            }
        }
    }

    /**
     * 모든 요소 삭제
     */
    fun clearAll() {
        _elements.clear()
        _selectedElementId.value = null
    }

    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}