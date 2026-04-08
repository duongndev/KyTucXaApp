package com.duongnd.kytucxa.core.navigations.graph.root

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.duongnd.kytucxa.core.navigations.Graphs
import com.duongnd.kytucxa.core.navigations.Screen
import com.duongnd.kytucxa.core.navigations.graph.auth.authNavGraph
import com.duongnd.kytucxa.core.navigations.graph.main.mainNavGraph
import com.duongnd.kytucxa.core.navigations.graph.registration.registrationNavGraph
import com.duongnd.kytucxa.core.navigations.splash.splashNavGraph
import com.duongnd.kytucxa.feature.MainViewModel
import com.duongnd.kytucxa.feature.profile.UpdateProfileScreen
import kotlinx.coroutines.launch

@Composable
fun KyTucXaAppNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    var showExpiredDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Lắng nghe sự kiện hết hạn token từ Interceptor
    LaunchedEffect(Unit) {
        mainViewModel.sessionEvent.events.collect {
            showExpiredDialog = true
        }
    }

    if (showExpiredDialog) {
        AlertDialog(
            onDismissRequest = { }, // Force user to click button
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = Color.Red
                )
            },
            title = {
                Text(
                    text = "Phiên đăng nhập hết hạn",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Phiên làm việc của bạn đã kết thúc. Vui lòng đăng nhập lại để tiếp tục sử dụng dịch vụ.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            // 1. Chỉ xóa session KHI người dùng xác nhận
                            mainViewModel.sessionManager.clearSession()
                            showExpiredDialog = false
                            
                            // 2. Sau đó mới điều hướng về Login
                            navController.navigate(Graphs.AUTH) {
                                popUpTo(Graphs.ROOT) { inclusive = true }
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ĐĂNG NHẬP LẠI")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    NavHost(
        navController = navController,
        startDestination = Graphs.SPLASH,
        route = Graphs.ROOT,
    ) {
        splashNavGraph(navController)
        authNavGraph(navController)
        mainNavGraph(navController)
        registrationNavGraph(navController)
        
        composable(Screen.UpdateProfile.route) {
            UpdateProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToSubmissionMethod = {
                    navController.navigate(Screen.SubmissionMethod.route)
                }
            )
        }
    }
}
