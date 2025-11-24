package com.example.slideit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.slideit.data.model.CanvasElement
import com.example.slideit.data.model.ElementType
import com.example.slideit.data.model.ShapeType

/**
 * 캔버스 요소 뷰
 */
@Composable
fun BoxScope.CanvasElementView(
    element: CanvasElement,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMove: (Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .offset(element.position.x.dp, element.position.y.dp)
            .size(element.size.x.dp, element.size.y.dp)
            .rotate(element.rotation)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF90CBFB), RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onMove(Offset(dragAmount.x, dragAmount.y))
                }
            }
    ) {
        when (element.type) {
            ElementType.TEXT -> {
                Text(
                    text = element.text ?: "",
                    fontSize = (element.fontSize ?: 16f).sp,
                    color = Color(element.color ?: 0xFF000000),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ElementType.SHAPE -> {
                when (element.shapeType) {
                    ShapeType.RECTANGLE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color(element.color ?: 0xFF90CBFB),
                                    RoundedCornerShape(8.dp)
                                )
                        )
                    }
                    ShapeType.CIRCLE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color(element.color ?: 0xFF90CBFB),
                                    CircleShape
                                )
                        )
                    }
                    else -> {}
                }
            }
            ElementType.IMAGE -> {
                AsyncImage(
                    model = element.imageUri,
                    contentDescription = "Canvas Image",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
