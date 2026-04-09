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
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Security
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import android.os.Build
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit,
    onNavigateToRegistration: (DraftResponse?) -> Unit,
    onNavigateToPending: (String) -> Unit,
    onNavigateToRequiresSupplement: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }
    val destination by viewModel.destination.collectAsState()

    var showCompleteProfileDialog by remember { mutableStateOf(false) }
    var showActiveAccountDialog by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showDraftFoundDialog by remember { mutableStateOf(false) }
    var isAnimationFinished by remember { mutableStateOf(false) }

    // Permission logic
    val permissionsToRequest = mutableListOf(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissionsToRequest)

    // Khởi tạo animation và check session
    LaunchedEffect(Unit) {
        viewModel.checkInitialRequirements()

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
    LaunchedEffect(destination, isAnimationFinished, permissionState.allPermissionsGranted) {
        // Ưu tiên hiển thị lỗi mạng hoặc yêu cầu quyền ngay lập tức
        if (destination is SplashDestination.NoInternet) {
            showNoInternetDialog = true
            return@LaunchedEffect
        }
        
        if (destination is SplashDestination.RequestPermissions) {
            if (!permissionState.allPermissionsGranted) {
                permissionState.launchMultiplePermissionRequest()
            }
            return@LaunchedEffect
        }

        if (isAnimationFinished && destination !is SplashDestination.Loading && 
            destination !is SplashDestination.CheckingNetwork && 
            destination !is SplashDestination.CheckingPermissions &&
            destination !is SplashDestination.Idle) {
            
            when (val dest = destination) {
                is SplashDestination.Login -> onNavigateToLogin()
                is SplashDestination.Home -> onNavigateToHome()
                is SplashDestination.UpdateProfile -> showCompleteProfileDialog = true
                is SplashDestination.Registration -> {
                    if (dest.draft?.hasDraft == true) {
                        showDraftFoundDialog = true
                    } else {
                        onNavigateToRegistration(null)
                    }
                }
                is SplashDestination.Pending -> onNavigateToPending(dest.registrationId)
                is SplashDestination.RequiresSupplement -> onNavigateToRequiresSupplement(dest.registrationId)
                is SplashDestination.Rejected -> {
                    // Hiển thị thông báo bị từ chối rồi cho làm lại
                    onNavigateToRegistration(null)
                }
                else -> {}
            }
        }
    }

    // Sau khi cấp quyền xong, check lại session
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted && destination is SplashDestination.RequestPermissions) {
            viewModel.checkInitialRequirements()
        }
    }

    // Dialog không có internet
    if (showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Rounded.NetworkCheck, null, tint = Color.Red) },
            title = { Text("Không có kết nối") },
            text = { Text("Vui lòng kiểm tra kết nối internet của bạn và thử lại.") },
            confirmButton = {
                KTXButton(text = "THỬ LẠI", onClick = {
                    showNoInternetDialog = false
                    viewModel.checkInitialRequirements()
                })
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // Dialog yêu cầu quyền (nếu bị từ chối)
    if (destination is SplashDestination.RequestPermissions && !permissionState.allPermissionsGranted) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Rounded.Security, null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Yêu cầu quyền truy cập") },
            text = { Text("Ứng dụng cần quyền Camera, Bộ nhớ và Thông báo để hoạt động đầy đủ tính năng.") },
            confirmButton = {
                KTXButton(text = "CẤP QUYỀN", onClick = {
                    permissionState.launchMultiplePermissionRequest()
                })
            },
            shape = RoundedCornerShape(28.dp)
        )
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
                        onNavigateToRegistration(null)
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

    // Dialog thông báo tìm thấy bản nháp đơn đăng ký
    if (showDraftFoundDialog) {
        val draft = (destination as? SplashDestination.Registration)?.draft
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Rounded.EditNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Tiếp tục đăng ký?",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Chúng tôi tìm thấy một đơn đăng ký đang làm dở (${draft?.progressPercent ?: 0}%). Bạn có muốn tiếp tục không?",
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                KTXButton(
                    text = "TIẾP TỤC",
                    onClick = {
                        showDraftFoundDialog = false
                        onNavigateToRegistration(draft)
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    showDraftFoundDialog = false
                    showActiveAccountDialog = true
                }) {
                    Text("LÀM MỚI", color = Color.Gray)
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
