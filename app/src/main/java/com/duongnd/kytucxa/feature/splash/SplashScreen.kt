package com.duongnd.kytucxa.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.rounded.AssignmentInd
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }
    val destination by viewModel.destination.collectAsState()

    var showCompleteProfileDialog by remember { mutableStateOf(false) }
    var showActiveAccountDialog by remember { mutableStateOf(false) }
    var isAnimationFinished by remember { mutableStateOf(false) }

    // Khởi tạo animation và check session
    LaunchedEffect(Unit) {
        viewModel.checkUserSession()

        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                )
            )
        }

        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000)
            )
            delay(300)
            isAnimationFinished = true
        }
    }

    // Luồng điều hướng chính sau khi animation kết thúc
    LaunchedEffect(destination, isAnimationFinished) {
        if (isAnimationFinished && destination !is SplashDestination.Loading) {
            when (destination) {
                is SplashDestination.Login -> onNavigateToLogin()
                is SplashDestination.Home -> onNavigateToHome()

                // Bước 3: Thiếu thông tin -> Hiện Dialog yêu cầu cập nhật
                is SplashDestination.UpdateProfile -> {
                    showCompleteProfileDialog = true
                }

                // Bước 4: Tài khoản chưa xác thực (isAccountVerified = false)
                is SplashDestination.Registration -> {
                    showActiveAccountDialog = true
                }

                else -> {}
            }
        }
    }

    // Dialog thông báo thiếu thông tin hồ sơ (Bước 3)
    if (showCompleteProfileDialog) {
        CompleteProfileDialog(
            onConfirm = {
                showCompleteProfileDialog = false
                onNavigateToUpdateProfile()
            },
            onLogout = {
                showCompleteProfileDialog = false
                viewModel.logout()
                onNavigateToLogin()
            }
        )
    }

    // Dialog thông báo tài khoản chưa kích hoạt (Bước 4 - False)
    if (showActiveAccountDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Tài khoản chưa kích hoạt",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Thông tin của bạn đã đầy đủ. Tuy nhiên, tài khoản cần được Ban quản lý xác thực. Vui lòng nộp hồ sơ đăng ký nội trú để được xét duyệt.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                KTXButton(
                    text = "NỘP HỒ SƠ NGAY",
                    onClick = {
                        showActiveAccountDialog = false
                        onNavigateToRegistration()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    showActiveAccountDialog = false
                    viewModel.logout()
                    onNavigateToLogin()
                }) {
                    Text("ĐĂNG XUẤT", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // UI của màn hình Splash
    SplashContent(scale.value, progress.value)
}

@Composable
private fun SplashContent(scale: Float, progress: Float) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            Color(0xFF001F54)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Apartment,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "KTX SINH VIÊN",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(200.dp)
                    .height(6.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CompleteProfileDialog(onConfirm: () -> Unit, onLogout: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            Icon(
                imageVector = Icons.Rounded.AssignmentInd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Hồ sơ chưa hoàn thiện",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Vui lòng cập nhật đầy đủ thông tin cá nhân và thông tin sinh viên để tiếp tục sử dụng dịch vụ.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            KTXButton(
                text = "CẬP NHẬT NGAY",
                onClick = onConfirm
            )
        },
        dismissButton = {
            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ĐĂNG XUẤT", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
