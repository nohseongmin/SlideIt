package com.example.slideit

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.slideit.data.model.BusinessCard
import com.example.slideit.ui.components.EditorTypeSelectionDialog
import com.example.slideit.ui.screens.CameraScreen
import com.example.slideit.ui.screens.CardCanvasEditorScreen
import com.example.slideit.ui.screens.CardEditorScreen
import com.example.slideit.ui.screens.CardShareScreen
import com.example.slideit.ui.screens.CardStorageScreen
import com.example.slideit.ui.screens.ProfileScreen
import com.example.slideit.ui.screens.SettingsScreen
import com.example.slideit.ui.theme.SlideITTheme
import com.example.slideit.util.ParsedCardInfo
import com.example.slideit.util.PreferencesManager
import com.example.slideit.util.TextRecognitionUtil
import com.example.slideit.viewmodel.CardViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val preferencesManager by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by preferencesManager.isDarkModeEnabled.collectAsStateWithLifecycle(initialValue = false)

            SlideITTheme(darkTheme = isDarkMode) {
                SlideITApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreenSizes
@Composable
fun SlideITApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: CardViewModel = viewModel()

    // State variables that need to be shared across different screens
    var parsedCardInfo by remember { mutableStateOf<ParsedCardInfo?>(null) }
    var cardToEdit by remember { mutableStateOf<BusinessCard?>(null) }
    var isMyCardEditor by remember { mutableStateOf(false) }
    var showEditorDialog by remember { mutableStateOf(false) }

    // This determines if the bottom nav should be shown
    val showBottomNav = currentDestination?.route in AppDestinations.entries.map { it.screen.route }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val extractedText = TextRecognitionUtil.extractTextFromImage(context, it)
                    parsedCardInfo = TextRecognitionUtil.parseBusinessCardText(extractedText)
                    navController.navigate(Screen.CardEditor.route)
                } catch (e: Exception) {
                    e.printStackTrace()
                    parsedCardInfo = null
                    navController.navigate(Screen.CardEditor.route)
                }
            }
        }
    }
    val onPickImage = { imagePickerLauncher.launch("image/*") }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar {
                        AppDestinations.entries.forEach { dest ->
                            NavigationBarItem(
                                icon = { Icon(dest.icon, contentDescription = dest.label) },
                                label = { Text(dest.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == dest.screen.route } == true,
                                onClick = {
                                    navController.navigate(dest.screen.route) {
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
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Share.route, // Default to Share screen
                modifier = Modifier.padding(innerPadding)
            ) {
                // Main screens (from the old pager)
                composable(Screen.Storage.route) {
                    CardStorageScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        onNavigateToEditor = {
                            cardToEdit = null
                            isMyCardEditor = false
                            showEditorDialog = true
                        },
                        onNavigateToEditCard = { card ->
                            cardToEdit = card
                            navController.navigate(Screen.CardEditor.route)
                        },
                        onPickImage = onPickImage,
                        onNavigateToCamera = { navController.navigate(Screen.Camera.route) }
                    )
                }
                composable(Screen.Share.route) {
                    CardShareScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        onNavigateToEditor = {
                            cardToEdit = null
                            isMyCardEditor = true
                            showEditorDialog = true
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        onNavigateToEditor = { isMyCard, card ->
                            cardToEdit = card
                            isMyCardEditor = isMyCard
                            showEditorDialog = true
                        },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }

                // Secondary screens (from the old NavHost)
                composable(Screen.CardEditor.route) {
                    CardEditorScreen(
                        modifier = Modifier.fillMaxSize(),
                        cardToEdit = cardToEdit,
                        parsedCardInfo = parsedCardInfo,
                        isMyCard = false,
                        viewModel = viewModel,
                        onNavigateBack = {
                            cardToEdit = null
                            parsedCardInfo = null
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
                        },
                        onSaveMyCard = {
                            cardToEdit = null
                            navController.navigate(Screen.Share.route) {
                                popUpTo(Screen.Share.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable("canvas_editor") {
                    CardCanvasEditorScreen(
                        modifier = Modifier.fillMaxSize(),
                        cardToEdit = cardToEdit,
                        isMyCard = isMyCardEditor,
                        cardViewModel = viewModel,
                        onNavigateBack = {
                            cardToEdit = null
                            navController.navigateUp()
                        },
                        onSaveMyCard = {
                            cardToEdit = null
                            navController.navigate(Screen.Share.route) {
                                popUpTo(Screen.Share.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(Screen.Camera.route) {
                    CameraScreen(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptured = { uri ->
                            coroutineScope.launch {
                                val ocrData = try {
                                    TextRecognitionUtil.extractTextFromImage(context, uri)
                                        .let { TextRecognitionUtil.parseBusinessCardText(it) }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }
                                parsedCardInfo = ocrData
                                // Navigate to editor, replacing camera screen
                                navController.navigate(Screen.CardEditor.route) {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            }
                        },
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }

        // Editor selection dialog remains at the top level
        if (showEditorDialog) {
            EditorTypeSelectionDialog(
                onDismiss = { showEditorDialog = false },
                onSimpleEditorSelected = {
                    showEditorDialog = false
                    navController.navigate(if (isMyCardEditor) Screen.MyCardEditor.route else Screen.CardEditor.route)
                },
                onCanvasEditorSelected = {
                    showEditorDialog = false
                    navController.navigate("canvas_editor")
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
    object Camera : Screen("camera")
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