package com.duongnd.kytucxa.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.rounded.AssignmentInd
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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

    LaunchedEffect(Unit) {
        viewModel.checkUserSession()

        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = OvershootInterpolator().toEasing()
                )
            )
        }

        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2500)
            )
            delay(500)
            isAnimationFinished = true
        }
    }

    LaunchedEffect(destination, isAnimationFinished) {
        if (isAnimationFinished && destination !is SplashDestination.Loading) {
            when (destination) {
                is SplashDestination.Login -> onNavigateToLogin()
                is SplashDestination.Home -> onNavigateToHome()
                is SplashDestination.Registration -> {
                    showActiveAccountDialog = true
                }
                is SplashDestination.UpdateProfile -> {
                    showCompleteProfileDialog = true
                }
                else -> onNavigateToLogin()
            }
        }
    }

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

    if (showActiveAccountDialog){
        AlertDialog(
            onDismissRequest = { showActiveAccountDialog = false },
            icon = {
                Icon(
                    Icons.Rounded.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text(text = "Tài khoản chưa kích hoạt", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Thông tin của bạn đã đầy đủ. Tuy nhiên, tài khoản cần được xác thực bởi Ban quản lý. Vui lòng nộp hồ sơ đăng ký nội trú để được xét duyệt kích hoạt tài khoản.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                KTXButton(
                    text = "NỘP HỒ SƠ NGAY",
                    onClick = {
                        onNavigateToRegistration()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showActiveAccountDialog = false }) {
                    Text("ĐỂ SAU", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            Color(0xFF001F54)
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        val logoSize = (screenHeight * 0.15f).coerceIn(100.dp, 160.dp)
        val progressWidth = (screenWidth * 0.7f).coerceIn(250.dp, 400.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AppLogo(scale.value, size = logoSize)

            Spacer(modifier = Modifier.height(24.dp))

            AppTitle()

            Spacer(modifier = Modifier.height(screenHeight * 0.08f))

            FancyLinearProgress(
                progress = progress.value,
                modifier = Modifier.width(progressWidth)
            )

        }

        SplashFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
fun CompleteProfileDialog(onConfirm: () -> Unit, onLogout: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        icon = {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.AssignmentInd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "Hồ sơ chưa được xác thực",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "Tài khoản của bạn chưa được xác thực hồ sơ đăng ký ký túc xá. Vui lòng cập nhật đầy đủ thông tin để tiếp tục sử dụng dịch vụ.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("CẬP NHẬT HỒ SƠ", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "ĐĂNG XUẤT", 
                    color = Color.Gray, 
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}

@Composable
private fun AppLogo(scale: Float, size: Dp) {
    Icon(
        imageVector = Icons.Default.Apartment,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(size)
            .scale(scale)
    )
}

@Composable
private fun AppTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "KTX SINH VIÊN",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            fontSize = 32.sp
        )

        Text(
            text = "Hệ thống ký túc xá thông minh",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun FancyLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = ""
    )

    val transition = rememberInfiniteTransition(label = "")
    val highlightOffset by transition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = ""
    )

    val baseGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF22D3EE),
            Color(0xFF3B82F6),
            Color(0xFF6366F1)
        )
    )

    val highlightBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.6f),
            Color.Transparent
        ),
        startX = highlightOffset * 600f,
        endX = highlightOffset * 600f + 200f
    )

    val loadingText by produceState(initialValue = "") {
        val messages = listOf(
            "Đang tải dữ liệu",
            "Đang kết nối hệ thống",
            "Đang chuẩn bị ứng dụng"
        )

        var index = 0
        var dots = 0

        while (true) {
            value = messages[index] + ".".repeat(dots)
            dots++

            if (dots > 3) {
                dots = 0
                index = (index + 1) % messages.size
            }

            delay(500)
        }
    }

    Column(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = loadingText,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.White.copy(alpha = 0.1f))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(baseGradient)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(highlightBrush)
            )
        }
    }
}

@Composable
private fun SplashFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Powered by duongnd",
            color = Color.White.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "v1.0.0",
            color = Color.White.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

class OvershootInterpolator(private val tension: Float = 2f) {
    fun toEasing() = Easing { x ->
        val t = x - 1f
        t * t * ((tension + 1f) * t + tension) + 1f
    }
}
