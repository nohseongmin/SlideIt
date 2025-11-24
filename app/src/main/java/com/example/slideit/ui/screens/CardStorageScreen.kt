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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.window.Dialog
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.ui.components.CardRenderer
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
    onPickImage: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {}
) {
    val cardViewModel: CardViewModel = viewModel
    var selectedCardId by remember { mutableStateOf<String?>(null) }
    var showAddMenu by remember { mutableStateOf(false) }
    var cardToPreview by remember { mutableStateOf<BusinessCard?>(null) }

    // ViewModel로부터 상태 수집
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val receivedCards by viewModel.receivedCards.collectAsStateWithLifecycle(initialValue = emptyList())

    // 표시할 카드 결정 (검색어 유무에 따라)
    val cardsToShow = if (searchQuery.isBlank()) {
        receivedCards
    } else {
        // 검색 결과에서 받은 명함만 필터링
        searchResults.filter { !it.isMyCard }
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
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
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
                        text = "총 ${cardsToShow.size}장의 명함",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                items(cardsToShow) { card ->
                    CardItem(
                        card = card,
                        isExpanded = selectedCardId == card.id,
                        viewModel = cardViewModel,
                        onClick = {
                            selectedCardId = if (selectedCardId == card.id) null else card.id
                        },
                        onEdit = { onNavigateToEditCard(card) },
                        onDelete = { viewModel.deleteCard(card) },
                        onPreview = { cardToPreview = card }
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

                    // 카메라로 촬영
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAddMenu = false
                                onNavigateToCamera()
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "카메라로 촬영",
                            tint = Color(0xFF90CBFB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "카메라로 촬영",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "카메라로 명함을 촬영합니다",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    // 갤러리에서 선택
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
                            Icons.Default.Image,
                            contentDescription = "갤러리에서 선택",
                            tint = Color(0xFF90CBFB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "갤러리에서 선택",
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

        // 명함 상세보기 다이얼로그
        cardToPreview?.let { card ->
            Dialog(
                onDismissRequest = { cardToPreview = null }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    CardRenderer(
                        card = card,
                        cardWidth = 300.dp,
                        showRotated = false
                    )
                }
            }
        }
    }
}

@Composable
private fun CardItem(
    card: BusinessCard,
    isExpanded: Boolean,
    viewModel: CardViewModel,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPreview: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = card.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(card.textColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // 즐겨찾기 아이콘
                            IconButton(
                                onClick = { viewModel.toggleFavorite(card.id, !card.isFavorite) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    if (card.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "즐겨찾기",
                                    tint = if (card.isFavorite) Color(0xFFFFD700) else Color(card.textColor).copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
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

                        // 상세보기 버튼
                        Button(
                            onClick = onPreview,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF90CBFB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("명함 크게 보기")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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
                                onClick = { showDeleteDialog = true },
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

        // 삭제 확인 다이얼로그
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("명함 삭제") },
                text = { Text("'${card.name}'님의 명함을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("취소")
                    }
                }
            )
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