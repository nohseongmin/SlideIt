package com.example.slideit.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.data.model.CanvasCardData
import com.example.slideit.data.model.CanvasElement
import com.example.slideit.data.model.ElementType
import com.example.slideit.data.model.ShapeType
import com.example.slideit.viewmodel.CanvasEditorViewModel
import com.example.slideit.viewmodel.CardViewModel
import com.example.slideit.ui.components.CanvasElementView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCanvasEditorScreen(
    modifier: Modifier = Modifier,
    cardToEdit: BusinessCard? = null,
    isMyCard: Boolean = false,
    cardViewModel: CardViewModel = viewModel(),
    canvasViewModel: CanvasEditorViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveMyCard: () -> Unit = {}
) {
    val context = LocalContext.current
    val backgroundColor by canvasViewModel.backgroundColor.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var showTextDialog by remember { mutableStateOf(false) }
    var showShapeMenu by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            canvasViewModel.addImageElement(it.toString())
        }
    }

    LaunchedEffect(cardToEdit) {
        cardToEdit?.canvasData?.let { jsonData ->
            val canvasData = CanvasCardData.fromJson(jsonData)
            canvasData?.let {
                canvasViewModel.fromCardElements(it.elements)
                canvasViewModel.setBackgroundColor(it.backgroundColor)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (cardToEdit == null) "새 명함 디자인" else "명함 수정",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                // Elements are now handled directly in portrait coordinates
                                val portraitElements = canvasViewModel.toCardElements()
                                
                                val canvasData = CanvasCardData(
                                    elements = portraitElements,
                                    backgroundColor = backgroundColor,
                                    width = 680f, // Portrait width
                                    height = 1080f // Portrait height
                                )

                                val cardToSave = cardToEdit?.copy(
                                    editorType = "CANVAS",
                                    canvasData = canvasData.toJson(),
                                    backgroundColor = backgroundColor,
                                    lastModifiedAt = System.currentTimeMillis()
                                ) ?: BusinessCard(
                                    name = "무제",
                                    position = "",
                                    department = "",
                                    company = "",
                                    email = "",
                                    phone = "",
                                    address = "",
                                    backgroundColor = backgroundColor,
                                    templateId = "canvas",
                                    editorType = "CANVAS",
                                    canvasData = canvasData.toJson(),
                                    isMyCard = isMyCard
                                )

                                cardViewModel.insertCard(cardToSave)

                                if (isMyCard) {
                                    cardViewModel.refreshMyCard()
                                    onSaveMyCard()
                                } else {
                                    onNavigateBack()
                                }
                            }
                        },
                        enabled = true
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "저장")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B7FD6),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            CanvasToolbar(
                onAddText = { showTextDialog = true },
                onAddImage = { imagePickerLauncher.launch("image/*") },
                onAddShape = { showShapeMenu = true },
                onChangeBackground = { showColorPicker = true },
                onDeleteSelected = { canvasViewModel.deleteSelectedElement() },
                hasSelection = canvasViewModel.selectedElementId.collectAsStateWithLifecycle().value != null
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F3FF),
                            Color(0xFFEDE7F6)
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CanvasArea(
                    viewModel = canvasViewModel,
                    onElementSelected = { }
                )
            }
        }
    }

    if (showTextDialog) {
        TextInputDialog(
            onDismiss = { showTextDialog = false },
            onConfirm = { text, fontSize, color ->
                canvasViewModel.addTextElement(
                    text = text,
                    fontSize = fontSize,
                    color = color
                )
                showTextDialog = false
            }
        )
    }

    if (showShapeMenu) {
        ShapeSelectionDialog(
            onDismiss = { showShapeMenu = false },
            onShapeSelected = { shapeType, color ->
                canvasViewModel.addShapeElement(
                    shapeType = shapeType,
                    color = color
                )
                showShapeMenu = false
            }
        )
    }

    if (showColorPicker) {
        BackgroundColorDialog(
            currentColor = backgroundColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                canvasViewModel.setBackgroundColor(color)
                showColorPicker = false
            }
        )
    }
}

@Composable
fun CanvasArea(
    viewModel: CanvasEditorViewModel,
    onElementSelected: (CanvasElement) -> Unit
) {
    val backgroundColor by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val selectedElementId by viewModel.selectedElementId.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(680f / 1080f) // Editor is portrait
            .clip(RoundedCornerShape(16.dp))
            .background(Color(backgroundColor))
            .border(2.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.deselectElement()
                    }
                )
            }
    ) {
        viewModel.elements.forEach { element ->
            CanvasElementView(
                element = element,
                isSelected = element.id == selectedElementId,
                onSelect = {
                    viewModel.selectElement(element.id)
                    onElementSelected(element)
                },
                onMove = { offset ->
                    viewModel.moveElement(element.id, offset)
                },
                onResize = { dragAmount ->
                    viewModel.resizeElement(element.id, dragAmount)
                },
                onRotate = { newRotation ->
                    viewModel.rotateElement(element.id, newRotation)
                },
                onRotateBy = { delta ->
                    viewModel.rotateElementBy(element.id, delta)
                }
            )
        }
    }
}

@Composable
fun CanvasToolbar(
    onAddText: () -> Unit,
    onAddImage: () -> Unit,
    onAddShape: () -> Unit,
    onChangeBackground: () -> Unit,
    onDeleteSelected: () -> Unit,
    hasSelection: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarButton(icon = Icons.Default.TextFields, label = "텍스트", onClick = onAddText)
            ToolbarButton(icon = Icons.Default.Image, label = "이미지", onClick = onAddImage)
            ToolbarButton(icon = Icons.Default.Square, label = "도형", onClick = onAddShape)
            ToolbarButton(icon = Icons.Default.Palette, label = "배경", onClick = onChangeBackground)
            ToolbarButton(
                icon = Icons.Default.Delete,
                label = "삭제",
                onClick = onDeleteSelected,
                enabled = hasSelection,
                tint = if (hasSelection) Color(0xFFEF4444) else Color.Gray
            )
        }
    }
}

@Composable
fun ToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color = Color(0xFF8B7FD6)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (enabled) tint else Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun TextInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Long) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var fontSize by remember { mutableStateOf(16f) }
    var color by remember { mutableStateOf(0xFF000000) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("텍스트 추가") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("텍스트") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("크기: ${fontSize.toInt()}sp")
                Slider(
                    value = fontSize,
                    onValueChange = { fontSize = it },
                    valueRange = 12f..48f,
                    steps = 35
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text, fontSize, color) },
                enabled = text.isNotEmpty()
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ShapeSelectionDialog(
    onDismiss: () -> Unit,
    onShapeSelected: (ShapeType, Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("도형 선택") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onShapeSelected(ShapeType.RECTANGLE, 0xFF90CBFB) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("사각형")
                }
                Button(
                    onClick = { onShapeSelected(ShapeType.CIRCLE, 0xFF90CBFB) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("원")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun BackgroundColorDialog(
    currentColor: Long,
    onDismiss: () -> Unit,
    onColorSelected: (Long) -> Unit
) {
    val colors = listOf(
        0xFFFFFFFF, 0xFFF5F5F5, 0xFFE5E5E5,
        0xFFEF4444, 0xFFF97316, 0xFFF59E0B,
        0xFF84CC16, 0xFF22C55E, 0xFF10B981,
        0xFF06B6D4, 0xFF0EA5E9, 0xFF3B82F6,
        0xFF8B5CF6, 0xFFA855F7, 0xFFEC4899
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("배경 색상 선택") },
        text = {
            Column {
                colors.chunked(3).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(Color(color), RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (color == currentColor) 3.dp else 1.dp,
                                        color = if (color == currentColor) Color(0xFF8B7FD6) else Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}