package com.duongnd.kytucxa.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.rounded.AssignmentInd
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Security
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftRegistrationDTO
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import android.os.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.core.ui.theme.KyTucXaTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.duongnd.kytucxa.core.ui.components.KTXDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit,
    onNavigateToRegistration: (DraftRegistrationDTO?) -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToPending: (String) -> Unit,
    onNavigateToRequiresSupplement: (String) -> Unit,
    onNavigateToOfflineInstructions: () -> Unit,
    onNavigateToStep1Residence: () -> Unit,
    onNavigateToStep2Temporary: () -> Unit,
    onNavigateToStep3Documents: () -> Unit,
    onNavigateToSubmitReady: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }
    val destination by viewModel.destination.collectAsState()

    var showCompleteProfileDialog by remember { mutableStateOf(false) }
    var showNoDraftDialog by remember { mutableStateOf(false) }
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
                    val draft = dest.currentResponse?.draft
                    // Kiểm tra tất cả các dấu hiệu của một đơn đang tồn tại
                    val hasExistingDraft = draft != null && (draft.id != null || draft.formData != null)

                    Timber.d("hasExistingDraft: $hasExistingDraft")

                    if (hasExistingDraft) {
                        showDraftFoundDialog = true
                    } else {
                        onNavigateToRegistration(null)
                    }
                }
                is SplashDestination.Tracking -> {
                    onNavigateToTracking(dest.currentResponse?.active?.id ?: "")
                }
                is SplashDestination.Step1Residence -> onNavigateToStep1Residence()
                is SplashDestination.Step2Temporary -> onNavigateToStep2Temporary()
                is SplashDestination.Step3Documents -> onNavigateToStep3Documents()
                is SplashDestination.SubmitReady -> onNavigateToSubmitReady()
                is SplashDestination.Rejected -> {
                    showNoDraftDialog = true
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
        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.NetworkCheck,
            iconTint = Color.Red,
            title = "Không có kết nối",
            description = {
                Text(
                    text = "Vui lòng kiểm tra kết nối internet của bạn và thử lại.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "THỬ LẠI",
            onConfirm = {
                showNoInternetDialog = false
                viewModel.checkInitialRequirements()
            },
            isCancelable = false
        )
    }

    // Dialog yêu cầu quyền (nếu bị từ chối)
    if (destination is SplashDestination.RequestPermissions && !permissionState.allPermissionsGranted) {
        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.Security,
            title = "Yêu cầu quyền truy cập",
            description = {
                Text(
                    text = "Ứng dụng cần quyền Camera, Bộ nhớ và Thông báo để hoạt động đầy đủ tính năng.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "CẤP QUYỀN",
            onConfirm = {
                permissionState.launchMultiplePermissionRequest()
            },
            isCancelable = false
        )
    }

    // Dialog thông báo thiếu thông tin hồ sơ (Bước 3)
    if (showCompleteProfileDialog) {
        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.AssignmentInd,
            title = "Hoàn thiện hồ sơ",
            description = {
                Text(
                    text = "Để đảm bảo quyền lợi và thực hiện các thủ tục hành chính, vui lòng cập nhật đầy đủ thông tin cá nhân và thông tin sinh viên.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "CẬP NHẬT NGAY",
            onConfirm = {
                showCompleteProfileDialog = false
                onNavigateToUpdateProfile()
            },
            dismissButtonText = "ĐĂNG XUẤT",
            onDismiss = {
                showCompleteProfileDialog = false
                viewModel.logout()
                onNavigateToLogin()
            },
            isCancelable = false
        )
    }

    // Dialog thông báo tìm thấy bản nháp đơn đăng ký
    if (showDraftFoundDialog) {
        val registration = (destination as? SplashDestination.Registration)?.currentResponse?.draft
        val progress = (destination as? SplashDestination.Registration)?.currentResponse?.progressPercent

        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.EditNote,
            title = "Tiếp tục đăng ký?",
            description = {
                val stepText = when (registration?.currentStep) {
                    0 -> "Hướng dẫn nộp hồ sơ"
                    1 -> "Thông tin nội trú"
                    2 -> "Thông tin tạm trú"
                    3 -> "Tải tài liệu minh chứng"
                    4 -> "Kiểm tra và gửi hồ sơ"
                    else -> "Thông tin nội trú"
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hệ thống ghi nhận bạn đang có một hồ sơ chưa hoàn tất.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mã hồ sơ: ${registration?.registrationFormCode ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Vị trí dừng: $stepText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Tiến độ hoàn thành: ${progress ?: 0}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            confirmButtonText = "TIẾP TỤC",
            onConfirm = {
                showDraftFoundDialog = false
                if (registration != null) {
                    val progressVal = progress ?: 0
                    when {
                        progressVal < 25 -> onNavigateToStep1Residence()
                        progressVal < 50 -> onNavigateToStep2Temporary()
                        progressVal < 75 -> onNavigateToStep3Documents()
                        else -> onNavigateToSubmitReady()
                    }
                } else {
                    onNavigateToRegistration(null)
                }
            },
            dismissButtonText = "XÓA & LÀM MỚI",
            onDismiss = {
                val formId = registration?.id
                if (formId != null) {
                    viewModel.deleteDraft(formId)
                }
                showDraftFoundDialog = false
            },
            isCancelable = false
        )
    }

    // Dialog thông báo chưa có đơn đăng ký hoặc bị từ chối (Bắt đầu mới)
    if (showNoDraftDialog) {
        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.AssignmentInd,
            title = "Đăng ký nội trú",
            description = {
                Text(
                    text = "Chào mừng bạn! Hãy bắt đầu hành trình tại ký túc xá bằng cách tạo đơn đăng ký nội trú ngay hôm nay.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "ĐĂNG KÝ NGAY",
            onConfirm = {
                showNoDraftDialog = false
                onNavigateToRegistration(null)
            },
            dismissButtonText = "ĐĂNG XUẤT",
            onDismiss = {
                showNoDraftDialog = false
                viewModel.logout()
                onNavigateToLogin()
            },
            isCancelable = false
        )
    }

    // UI của màn hình Splash
    SplashContent(
        scale = scale.value,
        progress = progress.value,
        destination = destination
    )
}

@Composable
private fun SplashContent(
    scale: Float,
    progress: Float,
    destination: SplashDestination
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0047BB),
            Color(0xFF002E7A),
            Color(0xFF001F54)
        )
    )

    val loadingText = remember(destination, progress) {
        when {
            progress < 0.3f -> "Khởi tạo hệ thống..."
            destination is SplashDestination.CheckingNetwork -> "Đang kiểm tra kết nối..."
            destination is SplashDestination.CheckingPermissions -> "Xác thực quyền truy cập..."
            destination is SplashDestination.Loading -> "Đang tải dữ liệu người dùng..."
            progress >= 1f -> "Hoàn tất kiểm tra"
            else -> "Vui lòng đợi trong giây lát..."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            // Logo Section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
            ) {
                // Background Glow for Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                )
                Icon(
                    imageVector = Icons.Default.Apartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "KTX SINH VIÊN",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Hệ thống KTX thông minh",
                color = Color.White,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Modern Progress Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress Bar Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Animated Gradient Progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(10.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF34D399), // Emerald 400
                                        Color(0xFF10B981), // Emerald 500
                                        Color(0xFF60A5FA)  // Blue 400
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Loading Text with Fade effect
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Text(
                    text = loadingText,
                    color = Color.White.copy(alpha = alpha),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Version Footer
        Text(
            text = "Phiên bản 1.0.0",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            color = Color.White.copy(alpha = 0.3f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashContentPreview() {
    KyTucXaTheme {
        SplashContent(
            scale = 1f,
            progress = 0.6f,
            destination = SplashDestination.Loading
        )
    }
}
