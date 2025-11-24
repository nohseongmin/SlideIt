package com.example.slideit.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.slideit.util.CsvUtil
import com.example.slideit.util.PreferencesManager
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 개인 설정 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferencesManager = remember { PreferencesManager(context) }

    val isDarkModeEnabled by preferencesManager.isDarkModeEnabled.collectAsStateWithLifecycle(initialValue = false)
    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var showExportSuccess by remember { mutableStateOf(false) }
    var showImportSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val allCards by viewModel.allCards.collectAsStateWithLifecycle(initialValue = emptyList())
    val receivedCards by viewModel.receivedCards.collectAsStateWithLifecycle(initialValue = emptyList())

    // CSV 내보내기 런처
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val result = CsvUtil.exportToCSV(context, allCards, it)
                if (result.isSuccess) {
                    showExportSuccess = true
                } else {
                    errorMessage = "내보내기 실패: ${result.exceptionOrNull()?.message}"
                }
            }
        }
    }

    // CSV 가져오기 런처
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val result = CsvUtil.importFromCSV(context, it)
                if (result.isSuccess) {
                    val cards = result.getOrNull() ?: emptyList()
                    viewModel.insertCards(cards)
                    showImportSuccess = true
                } else {
                    errorMessage = "가져오기 실패: ${result.exceptionOrNull()?.message}"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "개인 설정",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 계정 섹션
            Text(
                text = "계정",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.Person,
                        title = "프로필 편집",
                        subtitle = "이름, 이메일 등 변경",
                        onClick = { /* TODO: 프로필 편집 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Lock,
                        title = "비밀번호 변경",
                        subtitle = "보안을 위해 정기적으로 변경하세요",
                        onClick = { /* TODO: 비밀번호 변경 화면으로 이동 */ }
                    )
                }
            }

            // 앱 설정 섹션
            Text(
                text = "앱 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "알림",
                        subtitle = "명함 관련 알림 수신",
                        checked = isNotificationsEnabled,
                        onCheckedChange = { isNotificationsEnabled = it }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = "다크 모드",
                        subtitle = "어두운 테마 사용",
                        checked = isDarkModeEnabled,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                preferencesManager.setDarkMode(enabled)
                            }
                        }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Settings,
                        title = "언어",
                        subtitle = "한국어",
                        onClick = { /* TODO: 언어 설정 화면으로 이동 */ }
                    )
                }
            }

            // 데이터 관리 섹션
            Text(
                text = "데이터 관리",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.Upload,
                        title = "데이터 내보내기",
                        subtitle = "명함 데이터를 CSV 파일로 저장",
                        onClick = {
                            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                .format(Date())
                            exportLauncher.launch("slideit_cards_$timestamp.csv")
                        }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Download,
                        title = "데이터 가져오기",
                        subtitle = "CSV 파일에서 명함 데이터 불러오기",
                        onClick = {
                            importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv", "*/*"))
                        }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Delete,
                        title = "받은 명함 전체 삭제",
                        subtitle = "명함 보관함의 모든 명함 삭제 (${receivedCards.size}장)",
                        onClick = {
                            if (receivedCards.isNotEmpty()) {
                                showDeleteDialog = true
                            } else {
                                Toast.makeText(context, "삭제할 명함이 없습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // 기타 섹션
            Text(
                text = "기타",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "앱 정보",
                        subtitle = "버전 1.0.0",
                        onClick = { /* TODO: 앱 정보 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "도움말",
                        subtitle = "사용 가이드 및 FAQ",
                        onClick = { /* TODO: 도움말 화면으로 이동 */ }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        title = "이용약관",
                        subtitle = "서비스 이용약관 및 개인정보 처리방침",
                        onClick = { /* TODO: 이용약관 화면으로 이동 */ }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 로그아웃 버튼
            Button(
                onClick = { /* TODO: 로그아웃 처리 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "로그아웃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 성공/에러 스낵바
        errorMessage?.let { message ->
            LaunchedEffect(message) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                errorMessage = null
            }
        }

        if (showExportSuccess) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "데이터 내보내기 완료", Toast.LENGTH_SHORT).show()
                showExportSuccess = false
            }
        }

        if (showImportSuccess) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "데이터 가져오기 완료", Toast.LENGTH_SHORT).show()
                showImportSuccess = false
            }
        }

        // 명함 전체 삭제 확인 다이얼로그
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("명함 전체 삭제") },
                text = { Text("받은 명함 ${receivedCards.size}장을 모두 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                receivedCards.forEach { card ->
                                    viewModel.deleteCard(card)
                                }
                                Toast.makeText(context, "모든 명함이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                            }
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
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 배경
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF90CBFB).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF90CBFB),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 화살표
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 배경
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF90CBFB).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF90CBFB),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F1F1F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 스위치
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF90CBFB),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}
