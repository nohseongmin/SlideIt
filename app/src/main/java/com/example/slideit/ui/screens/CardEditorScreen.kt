package com.example.slideit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.data.model.CardTemplates
import com.example.slideit.util.ParsedCardInfo
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch

/**
 * 명함 에디터 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    modifier: Modifier = Modifier,
    cardToEdit: BusinessCard? = null,
    parsedCardInfo: ParsedCardInfo? = null,
    isMyCard: Boolean = false,
    viewModel: CardViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveMyCard: () -> Unit = {}
) {
    // OCR 결과가 있으면 우선 사용, 없으면 cardToEdit 사용
    var name by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.name ?: cardToEdit?.name ?: "") }
    var position by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.position ?: cardToEdit?.position ?: "") }
    var department by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.department ?: cardToEdit?.department ?: "") }
    var company by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.company ?: cardToEdit?.company ?: "") }
    var email by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.email ?: cardToEdit?.email ?: "") }
    var phone by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.phone ?: cardToEdit?.phone ?: "") }
    var address by remember(cardToEdit, parsedCardInfo) { mutableStateOf(parsedCardInfo?.address ?: cardToEdit?.address ?: "") }

    var selectedBackgroundColor by remember(cardToEdit) { mutableLongStateOf(cardToEdit?.backgroundColor ?: 0xFFFFFFFF) }
    var selectedTextColor by remember(cardToEdit) { mutableLongStateOf(cardToEdit?.textColor ?: 0xFF1F1F1F) }
    var selectedAccentColor by remember(cardToEdit) { mutableLongStateOf(cardToEdit?.accentColor ?: 0xFF90CBFB) }
    var selectedTemplateId by remember(cardToEdit) { mutableStateOf(cardToEdit?.templateId ?: "default") }
    var showTemplateSelector by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (cardToEdit == null) "새 명함 만들기" else "명함 수정",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF90CBFB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F9F9),
                            Color(0xFFEFEFEF)
                        )
                    )
                )
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 기본 정보 섹션
            Text(
                text = "기본 정보",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("이름 *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )

                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = { Text("직책 *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )

                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("부서") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )

                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("회사명 *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )
                }
            }

            // 연락처 정보 섹션
            Text(
                text = "연락처 정보",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("전화번호 *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("이메일 *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("주소") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF90CBFB),
                            focusedLabelColor = Color(0xFF90CBFB)
                        )
                    )
                }
            }

            // 템플릿 선택 섹션
            Text(
                text = "명함 템플릿",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Button(
                    onClick = { showTemplateSelector = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF90CBFB)
                    )
                ) {
                    Text("템플릿 선택하기")
                }
            }

            // 디자인 설정 섹션
            Text(
                text = "디자인 설정 (고급)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "배경 색상",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF555555)
                    )

                    ColorPicker(
                        selectedColor = selectedBackgroundColor,
                        onColorSelected = { selectedBackgroundColor = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "글자 색상",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF555555)
                    )

                    ColorPicker(
                        selectedColor = selectedTextColor,
                        onColorSelected = { selectedTextColor = it }
                    )
                }
            }

            // 미리보기
            Text(
                text = "미리보기",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(selectedBackgroundColor)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = name.ifEmpty { "이름" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(selectedTextColor)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$department | $position".takeIf {
                            department.isNotEmpty() || position.isNotEmpty()
                        } ?: "부서 | 직책",
                        fontSize = 12.sp,
                        color = Color(selectedTextColor).copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = phone.ifEmpty { "전화번호" },
                        fontSize = 11.sp,
                        color = Color(selectedTextColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = email.ifEmpty { "이메일" },
                        fontSize = 11.sp,
                        color = Color(selectedTextColor)
                    )
                }
            }

            // 저장 버튼
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 이름 + (회사/전화/이메일 중 하나) 있으면 저장
                        if (name.isNotEmpty() &&
                            (company.isNotEmpty() || phone.isNotEmpty() || email.isNotEmpty())
                        ) {
                            val cardToSave = cardToEdit?.copy(
                                name = name,
                                position = position,
                                department = department,
                                company = company,
                                email = email,
                                phone = phone,
                                address = address,
                                backgroundColor = selectedBackgroundColor,
                                textColor = selectedTextColor,
                                accentColor = selectedAccentColor,
                                templateId = selectedTemplateId,
                                isMyCard = isMyCard,
                                lastModifiedAt = System.currentTimeMillis()
                            ) ?: BusinessCard(
                                name = name,
                                position = position,
                                department = department,
                                company = company,
                                email = email,
                                phone = phone,
                                address = address,
                                backgroundColor = selectedBackgroundColor,
                                textColor = selectedTextColor,
                                accentColor = selectedAccentColor,
                                templateId = selectedTemplateId,
                                isMyCard = isMyCard,
                                createdAt = System.currentTimeMillis(),
                                lastModifiedAt = System.currentTimeMillis()
                            )

                            if (cardToEdit != null) {
                                viewModel.updateCard(cardToSave)
                            } else {
                                viewModel.insertCard(cardToSave)
                            }

                            // 내 명함이면 공유 화면으로, 아니면 뒤로가기
                            if (isMyCard) {
                                onSaveMyCard()
                            } else {
                                onNavigateBack()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF90CBFB)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotEmpty() &&
                        (company.isNotEmpty() || phone.isNotEmpty() || email.isNotEmpty())
            ) {
                Text(
                    text = "저장",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 템플릿 선택 다이얼로그
        if (showTemplateSelector) {
            AlertDialog(
                onDismissRequest = { showTemplateSelector = false },
                title = { Text("명함 템플릿 선택") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardTemplates.templates.forEach { template ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTemplateId = template.id
                                        selectedBackgroundColor = template.backgroundColor
                                        selectedTextColor = template.textColor
                                        selectedAccentColor = template.accentColor
                                        showTemplateSelector = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(template.backgroundColor)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = template.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(template.textColor)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = template.description,
                                        fontSize = 12.sp,
                                        color = Color(template.textColor).copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTemplateSelector = false }) {
                        Text("닫기")
                    }
                }
            )
        }
    }
}

@Composable
private fun ColorPicker(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        // 첫 번째 줄
        0xFFFFFFFF, 0xFFF5F5F5, 0xFFE5E5E5, 0xFFD4D4D4, 0xFFA3A3A3, 0xFF737373,
        // 두 번째 줄
        0xFF525252, 0xFF404040, 0xFF262626, 0xFF171717, 0xFF0F172A, 0xFF1E293B,
        // 세 번째 줄
        0xFFEF4444, 0xFFF97316, 0xFFF59E0B, 0xFFEAB308, 0xFF84CC16, 0xFF22C55E,
        // 네 번째 줄
        0xFF10B981, 0xFF14B8A6, 0xFF06B6D4, 0xFF0EA5E9, 0xFF3B82F6, 0xFF6366F1,
        // 다섯 번째 줄
        0xFF8B5CF6, 0xFFA855F7, 0xFFD946EF, 0xFFEC4899, 0xFFF43F5E, 0xFF1E3A8A
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.chunked(6).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                color = Color(color),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = if (color == selectedColor) 3.dp else 1.dp,
                                color = if (color == selectedColor) {
                                    Color(0xFF90CBFB)
                                } else {
                                    Color(0xFFE0E0E0)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onColorSelected(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (color == selectedColor) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = if (color == 0xFFFFFFFF || color == 0xFFF5F5F5 || color == 0xFFE5E5E5) {
                                    Color(0xFF90CBFB)
                                } else {
                                    Color.White
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
