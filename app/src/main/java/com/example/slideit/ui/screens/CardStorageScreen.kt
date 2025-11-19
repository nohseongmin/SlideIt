package com.example.slideit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.viewmodel.CardViewModel

/**
 * 명함 보관함 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardStorageScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = viewModel(),
    onNavigateToEditor: () -> Unit = {},
    onNavigateToEditCard: (BusinessCard) -> Unit = {},
    onPickImage: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    var showAddMenu by remember { mutableStateOf(false) }

    // 받은 명함만 표시
    val cards by viewModel.receivedCards.collectAsStateWithLifecycle(initialValue = emptyList())

    val filteredCards = remember(searchQuery, cards) {
        if (searchQuery.isBlank()) {
            cards
        } else {
            cards.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.company.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF9F9F9),
                            Color(0xFFEFEFEF)
                        )
                    )
                )
        ) {
            // 상단 헤더
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 40.dp, bottom = 16.dp)
                ) {
                    // 검색창
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("이름, 회사명으로 검색") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color(0xFF90CBFB),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            // 명함 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "총 ${filteredCards.size}장의 명함",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                items(filteredCards) { card ->
                    CardItem(
                        card = card,
                        isExpanded = selectedCardId == card.id,
                        onClick = {
                            selectedCardId = if (selectedCardId == card.id) null else card.id
                        },
                        onEdit = { onNavigateToEditCard(card) },
                        onDelete = { viewModel.deleteCard(card) }
                    )
                }
            }
        }

        // 명함 추가 FAB
        FloatingActionButton(
            onClick = { showAddMenu = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp),
            containerColor = Color(0xFF90CBFB),
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "명함 추가",
                tint = Color.White
            )
        }

        // 명함 추가 메뉴 바텀시트
        if (showAddMenu) {
            ModalBottomSheet(
                onDismissRequest = { showAddMenu = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "명함 추가",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )

                    // 직접 작성
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAddMenu = false
                                onNavigateToEditor()
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "직접 작성",
                            tint = Color(0xFF90CBFB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "직접 작성",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "명함 정보를 직접 입력합니다",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    // 사진으로 추가
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAddMenu = false
                                onPickImage()
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "사진으로 추가",
                            tint = Color(0xFF90CBFB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "사진으로 추가",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "갤러리에서 명함 사진을 선택합니다",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardItem(
    card: BusinessCard,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val elevation by animateFloatAsState(
        targetValue = if (isExpanded) 8f else 2f,
        label = "cardElevation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = if (isExpanded) -8f else 0f
            }
    ) {
        // 카드 뒷면 (적층 효과)
        repeat(2) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .offset(
                        x = ((index + 1) * 2).dp,
                        y = ((index + 1) * -4).dp
                    )
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.6f - index * 0.2f),
                        RoundedCornerShape(12.dp)
                    )
            )
        }

        // 메인 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(card.backgroundColor)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = card.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(card.textColor)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${card.department} | ${card.position}",
                            fontSize = 12.sp,
                            color = Color(card.textColor).copy(alpha = 0.7f)
                        )
                    }

                    Text(
                        text = card.company,
                        fontSize = 10.sp,
                        color = Color(card.textColor).copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 기본 정보
                InfoRow(
                    label = "M",
                    content = card.phone,
                    textColor = Color(card.textColor),
                    labelColor = Color(card.textColor).copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                InfoRow(
                    label = "E",
                    content = card.email,
                    textColor = Color(card.textColor),
                    labelColor = Color(card.textColor).copy(alpha = 0.6f)
                )

                // 확장된 정보
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(6.dp))
                        InfoRow(
                            label = "A",
                            content = card.address,
                            textColor = Color(card.textColor),
                            labelColor = Color(card.textColor).copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 편집/삭제 버튼
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onEdit,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF90CBFB)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "수정",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("수정")
                            }

                            OutlinedButton(
                                onClick = onDelete,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF4444)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "삭제",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("삭제")
                            }
                        }
                    }
                }

                // 하단 구분선
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(card.accentColor).copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    content: String,
    textColor: Color = Color(0xFF333333),
    labelColor: Color = Color(0xFF999999)
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = labelColor,
            modifier = Modifier.width(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = content,
            fontSize = 11.sp,
            color = textColor,
            lineHeight = 14.sp
        )
    }
}

