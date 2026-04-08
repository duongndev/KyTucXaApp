package com.duongnd.kytucxa.core.navigations.graph.main

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.core.navigations.bottom_nav.BottomNavItem
import com.duongnd.kytucxa.feature.checkin.CheckInScreen
import com.duongnd.kytucxa.feature.home.HomeScreen
import com.duongnd.kytucxa.feature.payment.PaymentHistoryScreen
import com.duongnd.kytucxa.feature.payment.PaymentScreen
import com.duongnd.kytucxa.feature.profile.ProfileScreen
import com.duongnd.kytucxa.feature.registration.DocumentUploadScreen
import com.duongnd.kytucxa.feature.registration.RegistrationFlowScreen
import com.duongnd.kytucxa.feature.registration.RegistrationViewModel
import com.duongnd.kytucxa.feature.registration.residence.ResidenceFormScreen
import com.duongnd.kytucxa.feature.room.RoomDetailScreen
import com.duongnd.kytucxa.feature.room.RoomScreen
import com.duongnd.kytucxa.feature.support.SupportScreen

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(route = Graphs.MAIN) {
        MainScreen(rootNavController = navController)
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Kích hoạt chế độ Edge-to-Edge cho toàn bộ MainScreen
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    // Kiểm tra cấu hình màn hình
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Room.route,
        Screen.PaymentHistory.route,
        Screen.Profile.route
    )

    val detailScreens = listOf(
        Screen.RoomDetail.route,
        Screen.Payment.route,
        Screen.RegistrationForm.route,
        Screen.DocumentUpload.route,
        Screen.RegistrationFlow.route
    )

    val showNav = currentRoute in bottomBarScreens
    val showTopBar = currentRoute in detailScreens

    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail cho máy tính bảng hoặc màn hình ngang rộng
        if (isTablet && showNav) {
            AppNavigationRail(navController = navController)
        }

        Scaffold(
            containerColor = Color.Transparent, // Để nội dung bên dưới hiển thị nền riêng
            bottomBar = {
                // Chỉ hiện Bottom Bar trên điện thoại (màn hình dọc/hẹp)
                if (!isTablet && showNav) {
                    BottomBarCustom(navController = navController)
                }
            }
        ) { innerPadding ->
            // Bỏ padding(innerPadding) để nội dung tràn toàn màn hình (Full Screen)
            // Lưu ý: Các màn hình con phải tự xử lý statusBarsPadding() và navigationBarsPadding()
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(onNavigateToPayment = {
                        navController.navigate(Screen.Payment.route)
                    })
                }
                composable(Screen.Room.route) {
                    RoomScreen(onRoomClick = { id ->
                        navController.navigate(Screen.RoomDetail.createRoute(id))
                    })
                }
                composable(Screen.CheckIn.route) {
                    CheckInScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.PaymentHistory.route) {
                    PaymentHistoryScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(onNavigateToUpdateProfile = {
                        rootNavController.navigate(Screen.UpdateProfile.route)
                    })
                }

                composable(
                    route = Screen.RoomDetail.route,
                    arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                ) {
                    RoomDetailScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Payment.route) {
                    PaymentScreen(
                        onSeeHistory = { navController.navigate(Screen.PaymentHistory.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Support.route) {
                    SupportScreen()
                }
                composable(Screen.RegistrationForm.route) { entry ->
                    // Sử dụng Graphs.MAIN từ rootNavController để share ViewModel giữa các bước trong Main
                    val parentEntry = remember(entry) {
                        rootNavController.getBackStackEntry(Graphs.MAIN)
                    }
                    val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
                    ResidenceFormScreen(
                        viewModel = viewModel,
                        onNext = { navController.navigate(Screen.DocumentUpload.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.DocumentUpload.route) { entry ->
                    val parentEntry = remember(entry) {
                        rootNavController.getBackStackEntry(Graphs.MAIN)
                    }
                    val viewModel = hiltViewModel<RegistrationViewModel>(parentEntry)
                    DocumentUploadScreen(
                        viewModel = viewModel,
                        onNext = { navController.navigate(Screen.RegistrationFlow.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.RegistrationFlow.route) {
                    RegistrationFlowScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(currentRoute: String?, navController: NavHostController) {
    val title = when {
        currentRoute?.startsWith("room/") == true -> "Chi tiết phòng"
        currentRoute == Screen.Payment.route -> "Thanh toán"
        currentRoute == Screen.RegistrationForm.route -> "Đăng ký thuê phòng"
        currentRoute == Screen.DocumentUpload.route -> "Tải lên hồ sơ"
        currentRoute == Screen.RegistrationFlow.route -> "Quy trình đăng ký"
        else -> ""
    }

    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun BottomBarCustom(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val primaryColor = Color(0xFF0061FF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.height(80.dp),
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            val leftItems = listOf(BottomNavItem.HOME, BottomNavItem.BOOKING)
            val rightItems = listOf(BottomNavItem.HISTORY, BottomNavItem.PROFILE)

            leftItems.forEach { item ->
                NavIcon(item, currentDestination, navController, primaryColor)
            }

            Spacer(modifier = Modifier.weight(1f))

            rightItems.forEach { item ->
                NavIcon(item, currentDestination, navController, primaryColor)
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(BottomNavItem.CHECKIN.screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            shape = CircleShape,
            containerColor = primaryColor,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .size(60.dp)
                .offset(y = (-30).dp)
        ) {
            Icon(
                imageVector = BottomNavItem.CHECKIN.icon,
                contentDescription = "Quét mã",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun AppNavigationRail(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val primaryColor = Color(0xFF0061FF)

    NavigationRail(
        containerColor = Color.White,
        header = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(BottomNavItem.CHECKIN.screen.route)
                },
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(BottomNavItem.CHECKIN.icon, contentDescription = "Scan")
            }
        },
        modifier = Modifier.fillMaxHeight()
    ) {
        val items = listOf(
            BottomNavItem.HOME,
            BottomNavItem.BOOKING,
            BottomNavItem.HISTORY,
            BottomNavItem.PROFILE
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                NavigationRailItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavIcon(
    item: BottomNavItem,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavHostController,
    primaryColor: Color
) {
    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    NavigationBarItem(
        selected = selected,
        onClick = {
            navController.navigate(item.screen.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(24.dp)) },
        label = { Text(text = item.label, fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = primaryColor,
            selectedTextColor = primaryColor,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}
