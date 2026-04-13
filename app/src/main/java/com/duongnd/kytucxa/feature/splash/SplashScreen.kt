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
    onNavigateToRegistration: (DraftResponse?) -> Unit,
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
                    val draft = dest.draft
                    // Kiểm tra tất cả các dấu hiệu của một đơn đang tồn tại
                    // Chấp nhận progressPercent = 0 là một đơn hợp lệ
                    val hasExistingDraft = draft != null && (draft.registrationForm != null ||
                            draft.existingFormId != null || draft.hasDraft || draft.progressPercent != null)

                    Timber.d("hasExistingDraft: $hasExistingDraft")

                    if (hasExistingDraft) {
                        showDraftFoundDialog = true
                    } else {
                        showNoDraftDialog = true
                    }

                }
                is SplashDestination.Pending -> onNavigateToPending(dest.registrationId!!)
                is SplashDestination.RequiresSupplement -> onNavigateToRequiresSupplement(dest.registrationId!!)
                is SplashDestination.OfflineInstructions -> onNavigateToOfflineInstructions()
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
            title = "Hồ sơ chưa hoàn thiện",
            description = {
                Text(
                    text = "Vui lòng cập nhật đầy đủ thông tin cá nhân và thông tin sinh viên để tiếp tục sử dụng dịch vụ.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "CẤP NHẬT NGAY",
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
        val draft = (destination as? SplashDestination.Registration)?.draft
        val registration = draft?.registrationForm

        KTXDialog(
            onDismissRequest = { },
            icon = Icons.Rounded.EditNote,
            title = "Tiếp tục nộp hồ sơ?",
            description = {
                val content = buildString {
                    // Ưu tiên lấy thông tin từ registrationForm hoặc existing thông tin
                    val code = registration?.registrationFormCode ?: draft?.existingFormCode ?: "N/A"
                    val progressVal = draft?.progressPercent ?: 0
                    val status = registration?.status ?: draft?.existingStatus ?: "draft"
                    val step = registration?.currentStep ?: 0

                    append("Hệ thống tìm thấy hồ sơ của bạn đang được thực hiện.\n")
                    append("Mã hồ sơ: $code\n")
                    append("Trạng thái: ${status.uppercase()}\n")
                    append("Tiến độ: $progressVal%")

                    when (step) {
                        0, 1 -> append("\n\nBạn đang ở bước điền thông tin nội trú.")
                        2 -> append("\n\nBạn đã xong thông tin nội trú, tiếp theo là thông tin tạm trú.")
                        3 -> append("\n\nBạn cần tải lên các tài liệu minh chứng để hoàn tất.")
                        4 -> append("\n\nHồ sơ đã sẵn sàng, bạn có thể kiểm tra và nhấn gửi ngay.")
                    }
                }
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButtonText = "TIẾP TỤC",
            onConfirm = {
                showDraftFoundDialog = false
                if (registration != null) {
                    // Nếu có object registrationForm đầy đủ, điều hướng theo step
                    when (registration.currentStep) {
                        0 -> onNavigateToOfflineInstructions()
                        1 -> onNavigateToStep1Residence()
                        2 -> onNavigateToStep2Temporary()
                        3 -> onNavigateToStep3Documents()
                        4 -> onNavigateToSubmitReady()
                        else -> onNavigateToRegistration(draft)
                    }
                } else {
                    // Nếu chỉ có ID đơn (trường hợp JSON lỗi nhưng có data), điều hướng về luồng chung
                    onNavigateToRegistration(draft)
                }
            },
            dismissButtonText = "LÀM MỚI",
            onDismiss = {
                val formId = registration?.id ?: draft?.existingFormId
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
                    text = "Bạn chưa có đơn đăng ký nội trú nào hoặc đơn cũ đã bị từ chối. Bắt đầu đăng ký ngay để giữ chỗ!",
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

            if (progress >= 1f) {
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đang kiểm tra dữ liệu...",
                    color = Color.White.copy(alpha = alpha),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
