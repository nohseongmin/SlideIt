package com.example.slideit

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.ui.screens.CardEditorScreen
import com.example.slideit.ui.screens.CardShareScreen
import com.example.slideit.ui.screens.CardStorageScreen
import com.example.slideit.ui.screens.ProfileScreen
import com.example.slideit.ui.screens.SettingsScreen
import com.example.slideit.ui.theme.SlideITTheme
import com.example.slideit.util.ParsedCardInfo
import com.example.slideit.util.TextRecognitionUtil
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlideITTheme {
                SlideITApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SlideITApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // OCR 결과 상태
    var parsedCardInfo by remember { mutableStateOf<ParsedCardInfo?>(null) }

    // 이미지 피커
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    // OCR 처리
                    val extractedText = TextRecognitionUtil.extractTextFromImage(context, it)
                    parsedCardInfo = TextRecognitionUtil.parseBusinessCardText(extractedText)

                    // 에디터로 이동
                    navController.navigate(Screen.CardEditor.route)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 에러 발생 시에도 빈 상태로 에디터 열기
                    parsedCardInfo = null
                    navController.navigate(Screen.CardEditor.route)
                }
            }
        }
    }

    val onPickImage = {
        imagePickerLauncher.launch("image/*")
    }

    // 하단 네비게이션이 표시되어야 하는 화면인지 확인
    val showBottomNav = currentDestination?.route in listOf(
        Screen.Storage.route,
        Screen.Share.route,
        Screen.Profile.route
    )

    if (showBottomNav) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == destination.screen.route
                    } == true

                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label,
                                tint = if (isSelected) {
                                    Color(0xFF90CBFB)
                                } else {
                                    Color.Gray
                                }
                            )
                        },
                        label = {
                            Text(
                                destination.label,
                                color = if (isSelected) {
                                    Color(0xFF90CBFB)
                                } else {
                                    Color.Gray
                                }
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(destination.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) {
            SlideITNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                onPickImage = onPickImage,
                parsedCardInfo = parsedCardInfo,
                onCardInfoUsed = { parsedCardInfo = null }
            )
        }
    } else {
        SlideITNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            onPickImage = onPickImage,
            parsedCardInfo = parsedCardInfo,
            onCardInfoUsed = { parsedCardInfo = null }
        )
    }
}

@Composable
fun SlideITNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onPickImage: () -> Unit = {},
    parsedCardInfo: ParsedCardInfo? = null,
    onCardInfoUsed: () -> Unit = {}
) {
    val viewModel: CardViewModel = viewModel()
    var cardToEdit by remember { mutableStateOf<BusinessCard?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Share.route,
        modifier = modifier
    ) {
        composable(Screen.Storage.route) {
            CardStorageScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onNavigateToEditor = {
                    cardToEdit = null
                    navController.navigate(Screen.CardEditor.route)
                },
                onNavigateToEditCard = { card ->
                    cardToEdit = card
                    navController.navigate(Screen.CardEditor.route)
                },
                onPickImage = onPickImage
            )
        }

        composable(Screen.Share.route) {
            CardShareScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onNavigateToEditor = { isMyCard ->
                    cardToEdit = null
                    navController.navigate(
                        if (isMyCard) Screen.MyCardEditor.route else Screen.CardEditor.route
                    )
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.CardEditor.route) {
            CardEditorScreen(
                modifier = Modifier.fillMaxSize(),
                cardToEdit = cardToEdit,
                parsedCardInfo = parsedCardInfo,
                viewModel = viewModel,
                onNavigateBack = {
                    cardToEdit = null
                    onCardInfoUsed()
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.MyCardEditor.route) {
            CardEditorScreen(
                modifier = Modifier.fillMaxSize(),
                cardToEdit = cardToEdit,
                isMyCard = true,
                viewModel = viewModel,
                onNavigateBack = {
                    cardToEdit = null
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Storage : Screen("storage")
    object Share : Screen("share")
    object Profile : Screen("profile")
    object CardEditor : Screen("card_editor")
    object MyCardEditor : Screen("my_card_editor")
    object Settings : Screen("settings")
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
) {
    STORAGE("명함 보관함", Icons.Default.Home, Screen.Storage),
    SHARE("명함 공유", Icons.Default.Share, Screen.Share),
    PROFILE("개인 설정", Icons.Default.Person, Screen.Profile),
}