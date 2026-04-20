package com.duongnd.kytucxa.feature.registration.tracking

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.duongnd.kytucxa.core.ui.theme.KyTucXaTheme
import com.duongnd.kytucxa.data.remote.dto.registration.current.active.ActiveRegistrationDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.tracking.TrackingDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.tracking.TrackingStatusDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.tracking.TrackingProgressDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.tracking.TrackingStagesDTO
import com.duongnd.kytucxa.core.ui.components.KTXDialog
import com.duongnd.kytucxa.core.utils.DateUtils

@Composable
fun RegistrationTrackingScreen(
    onUploadStampedForm: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onResubmit: (String) -> Unit,
    onSkip: () -> Unit,
    viewModel: RegistrationTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistrationTrackingScreen(
        uiState = uiState,
        onUploadStampedForm = onUploadStampedForm,
        onViewDetail = onViewDetail,
        onResubmit = onResubmit,
        onSkip = onSkip,
        onRefresh = { viewModel.getCurrentRegistration() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationTrackingScreen(
    uiState: RegistrationTrackingUiState,
    onUploadStampedForm: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onResubmit: (String) -> Unit,
    onSkip: () -> Unit,
    onRefresh: () -> Unit
) {
    BackHandler(enabled = true) { /* Chặn quay lại */ }

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Theo dõi hồ sơ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Làm mới",
                            tint = Color(0xFF0047BB)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier.padding(padding)
        ) {
            when {
                uiState.isLoading && uiState.currentRegistration == null -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = onRefresh,
                        onBack = onSkip
                    )
                }

                uiState.currentRegistration != null -> {
                    RegistrationTrackingContent(
                        currentResponse = uiState.currentRegistration,
                        onUploadStampedForm = onUploadStampedForm,
                        onViewDetail = onViewDetail,
                        onResubmit = onResubmit,
                        onSkip = onSkip
                    )
                }

                else -> {
                    EmptyContent(onBack = onSkip)
                }
            }
        }
    }
}

@Composable
fun RegistrationTrackingContent(
    currentResponse: CurrentResponse,
    onUploadStampedForm: (String) -> Unit,
    onViewDetail: (String) -> Unit,
    onResubmit: (String) -> Unit,
    onSkip: () -> Unit
) {
    val tracking = currentResponse.tracking
    val active = currentResponse.active
    val draft = currentResponse.draft

    var showMissingDocsDialog by remember { mutableStateOf(false) }
    var showStampedFormDialog by remember { mutableStateOf(false) }

    val isMissing = active?.isMissingDocuments == true ||
            draft?.isMissingDocuments == true ||
            active?.status in listOf("rejected", "need_correction", "correction_required", "incomplete") ||
            draft?.status in listOf("rejected", "need_correction", "correction_required", "incomplete")

    // Kiểm tra trạng thái nộp đơn có dấu
    val needsStampedForm = tracking?.stampedForm?.uploaded == false &&
            tracking.stampedForm.deadline != null

    val missingDocsList = remember(active, draft, isMissing) {
        val list = mutableListOf<String>()
        val missingFromApi = active?.missingDocuments ?: draft?.missingDocuments
        
        missingFromApi?.forEach { item ->
            when (item) {
                is String -> if (item.isNotBlank()) list.add(item)
                is Map<*, *> -> {
                    val label = item["label"] ?: item["name"] ?: item["type"]
                    if (label is String) list.add(label)
                }
            }
        }

        if (list.isEmpty() && isMissing) {
            val req = active?.requiredDocuments ?: draft?.requiredDocuments
            val docs = active?.documents ?: draft?.documents ?: emptyList()
            val uploadedTypes = docs.mapNotNull { it.type }

            if (req?.cccdFront == true && !uploadedTypes.contains("cccd_front")) list.add("Mặt trước CCCD")
            if (req?.cccdBack == true && !uploadedTypes.contains("cccd_back")) list.add("Mặt sau CCCD")
            if (req?.studentCard == true && !uploadedTypes.contains("student_card")) list.add("Thẻ sinh viên")
            if (req?.photo3x4 == true && !uploadedTypes.contains("photo_3x4")) list.add("Ảnh 3x4")
            if (req?.priorityDoc == true && !uploadedTypes.contains("priority_proof")) list.add("Giấy tờ ưu tiên")
            if (req?.stampedForm == true && !uploadedTypes.contains("stamped_form")) list.add("Đơn có dấu")

            if (list.isEmpty()) {
                list.add("Các giấy tờ theo yêu cầu của quản trị viên")
            }
        }
        list
    }

    LaunchedEffect(currentResponse) {
        if (isMissing && missingDocsList.isNotEmpty()) {
            showMissingDocsDialog = true
        } else if (needsStampedForm) {
            showStampedFormDialog = true
        }
    }

    val formId = active?.id ?: draft?.id ?: ""
    val formCode = tracking?.formCode ?: "N/A"

    val statusInfo = tracking?.status
    val progressInfo = tracking?.progress
    val stages = tracking?.stages ?: emptyList()
    val pendingActions = tracking?.pendingActions ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Thẻ trạng thái chính
        item {
            MainStatusCard(
                formCode = formCode,
                status = statusInfo?.label ?: "Đang xử lý",
                progressInfo = progressInfo,
                badge = statusInfo?.badge
            )
        }

        // 2. Các hành động cần xử lý
        if (pendingActions.isNotEmpty()) {
            item {
                PendingActionsSection(pendingActions, formId, onUploadStampedForm)
            }
        }

        // 3. Hiển thị quy trình các bước
        if (stages.isNotEmpty()) {
            item {
                ProcessStepper(stages)
            }
        }

        // 3.1. Danh sách hồ sơ còn thiếu (nếu có)
        if (isMissing && missingDocsList.isNotEmpty()) {
            item {
                MissingDocumentsSection(missingDocsList)
            }
        }

        // 4. Hướng dẫn nộp đơn trực tiếp
        if (active?.status == "pending_offline") {
            item {
                OfflineGuidanceCard()
            }
        }


        // 6. Nút xem chi tiết & Footer
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onViewDetail(formId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0047BB)
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFF0047BB).copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.Article,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xem chi tiết hồ sơ", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF64748B)
                    )
                ) {
                    Text("Bỏ qua", fontWeight = FontWeight.Medium)
                }

                Text(
                    "Cảm ơn bạn đã sử dụng dịch vụ của Ký túc xá\nPhiên bản ứng dụng 1.0.2",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showMissingDocsDialog && missingDocsList.isNotEmpty()) {
        KTXDialog(
            onDismissRequest = { showMissingDocsDialog = false },
            title = "Hồ sơ còn thiếu",
            confirmButtonText = "Đã hiểu",
            onConfirm = { showMissingDocsDialog = false },
            icon = Icons.Rounded.WarningAmber,
            iconTint = Color(0xFFEF4444)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Vui lòng bổ sung các giấy tờ sau để tiếp tục xử lý hồ sơ:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    missingDocsList.forEach { docName ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFFEF4444), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                docName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStampedFormDialog) {
        val stampedInfo = tracking?.stampedForm
        KTXDialog(
            onDismissRequest = { showStampedFormDialog = false },
            title = "Yêu cầu nộp đơn có dấu",
            confirmButtonText = "Nộp ngay",
            dismissButtonText = "Để sau",
            onConfirm = {
                showStampedFormDialog = false
                onUploadStampedForm(formId)
            },
            onDismiss = { showStampedFormDialog = false },
            icon = Icons.Rounded.ContentPaste,
            iconTint = Color(0xFF0047BB)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Hồ sơ trực tuyến của bạn đã được duyệt sơ bộ. Vui lòng tải lên ảnh đơn đăng ký đã có dấu xác nhận.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                if (stampedInfo?.deadline != null) {
                    Surface(
                        color = if (stampedInfo.isOverdue == true) Color(0xFFFEF2F2) else Color(0xFFF0F9FF),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.Info,
                                contentDescription = null,
                                tint = if (stampedInfo.isOverdue == true) Color(0xFFEF4444) else Color(0xFF0047BB),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Hạn chót bổ sung",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = DateUtils.formatString(stampedInfo.deadline),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stampedInfo.isOverdue == true) Color(0xFFB91C1C) else Color(0xFF0C4A6E)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissingDocumentsSection(missingDocs: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Hồ sơ cần bổ sung",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            missingDocs.forEach { doc ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFEF4444).copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        doc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MainStatusCard(
    formCode: String,
    status: String,
    progressInfo: TrackingProgressDTO?,
    badge: String? = null
) {
    // Animation cho phần trăm tiến độ
    val animatedProgress by animateFloatAsState(
        targetValue = (progressInfo?.percent ?: 0) / 100f,
        animationSpec = tween(durationMillis = 1500),
        label = "ProgressAnimation"
    )

    // Hiệu ứng loading (shimmer) chạy liên tục trên thanh progress
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF10B981),
            Color(0xFF6EE7B7),
            Color(0xFF10B981),
        ),
        start = Offset(shimmerTranslate, 0f),
        end = Offset(shimmerTranslate + 300f, 0f),
        tileMode = TileMode.Clamp
    )

    // Tối ưu hóa việc tính toán chuỗi văn bản
    val stepsText = remember(progressInfo?.userCompleted, progressInfo?.totalSteps) {
        if (progressInfo?.userCompleted != null && progressInfo.totalSteps != null) {
            "${progressInfo.userCompleted}/${progressInfo.totalSteps} bước hoàn tất"
        } else null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0047BB), Color(0xFF00358E))
                    )
                )
                .padding(24.dp)
        ) {
            // Header: Mã hồ sơ & Badge trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.HistoryEdu,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            formCode,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!badge.isNullOrEmpty()) {
                    Surface(
                        color = Color(0xFFFCD34D),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            badge.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(0xFF78350F),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Body: Trạng thái hiện tại
            Column {
                Text(
                    status,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Section: Modern Glassmorphism
            Surface(
                color = Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                "Tiến độ xử lý",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                stepsText ?: "Đang phân tích...",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            "${(animatedProgress * 100).toInt()}%",
                            color = Color(0xFF10B981),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Progress Bar with Shimmer Effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(14.dp)
                                .background(shimmerBrush, CircleShape)
                        )
                    }

                    // Next Action Hint
                    if (progressInfo != null && !progressInfo.nextAction.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFFCD34D),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Kế tiếp: ${progressInfo.nextAction}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessStepper(stages: List<TrackingStagesDTO>) {
    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    // Tìm index của bước hiện tại (bước đầu tiên chưa hoàn thành)
    val currentStepIndex = stages.indexOfFirst { !(it.completed ?: false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Text(
                "Tiến độ hồ sơ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            stages.forEachIndexed { index, stage ->
                val itemAlpha by animateFloatAsState(
                    targetValue = if (animationTriggered) 1f else 0f,
                    animationSpec = tween(durationMillis = 500, delayMillis = index * 100),
                    label = "StepAlpha"
                )
                val itemSlide by animateFloatAsState(
                    targetValue = if (animationTriggered) 0f else 20f,
                    animationSpec = tween(durationMillis = 500, delayMillis = index * 100),
                    label = "StepSlide"
                )

                Box(
                    modifier = Modifier.graphicsLayer {
                        alpha = itemAlpha
                        translationY = itemSlide
                    }
                ) {
                    StepItem(
                        title = stage.name ?: "N/A",
                        isDone = stage.completed ?: false,
                        hasNext = index < stages.size - 1,
                        isCurrent = index == currentStepIndex
                    )
                }
            }
        }
    }
}

@Composable
fun StepItem(title: String, isDone: Boolean, hasNext: Boolean, isCurrent: Boolean = false) {
    val iconColor by animateColorAsState(
        targetValue = when {
            isDone -> Color(0xFF10B981)
            isCurrent -> Color(0xFF0047BB)
            else -> Color(0xFFCBD5E1)
        },
        animationSpec = tween(durationMillis = 600),
        label = "IconColor"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isDone -> Color(0xFF1E293B)
            isCurrent -> Color(0xFF0047BB)
            else -> Color(0xFF94A3B8)
        },
        animationSpec = tween(durationMillis = 600),
        label = "TextColor"
    )

    // Pulse animation cho bước hiện tại
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (isCurrent && !isDone) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseScale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Row(modifier = Modifier.height(50.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
                if (isCurrent && !isDone) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = 1f - (pulseScale - 1f) * 2.5f
                            }
                            .background(Color(0xFF0047BB).copy(alpha = 0.3f), CircleShape)
                    )
                }

                AnimatedContent(
                    targetState = isDone,
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    },
                    label = "IconTransition"
                ) { done ->
                    Icon(
                        imageVector = if (done) {
                            Icons.Rounded.CheckCircle
                        } else if (isCurrent) {
                            Icons.Rounded.PendingActions
                        } else {
                            Icons.Rounded.ContentPaste
                        },
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(if (done) 24.dp else 20.dp)
                    )
                }
            }

            if (hasNext) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(Color(0xFFF1F5F9))
                ) {
                    val lineProgress by animateFloatAsState(
                        targetValue = if (isDone) 1f else 0f,
                        animationSpec = tween(durationMillis = 600),
                        label = "LineProgress"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(lineProgress)
                            .background(Color(0xFF10B981))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun PendingActionsSection(actions: List<String>, formId: String, onUpload: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.WarningAmber,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Cần xử lý",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155)
            )
        }

        actions.forEach { action ->
            ActionItemCard(
                text = action,
                onClick = { if (action.contains("Upload", true)) onUpload(formId) }
            )
        }
    }
}

@Composable
fun ActionItemCard(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFAFA),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFEE2E2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.PendingActions,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun OfflineGuidanceCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(1.dp, Color(0xFFFEF3C7)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFFD97706))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Lưu ý: Bạn cần mang đơn đăng ký có dấu xác nhận nộp trực tiếp tại Văn phòng Ký túc xá để hoàn tất.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF92400E),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color(0xFF0047BB), strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Đang cập nhật trạng thái...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun EmptyContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Rounded.HistoryEdu,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Không có hồ sơ nào",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Bạn chưa có yêu cầu đăng ký nào đang được thực hiện.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047BB))
        ) {
            Text("Quay lại Trang chủ")
        }
    }
}

@Composable
fun ErrorContent(error: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Rounded.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(error, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0047BB)
                ),
                border = BorderStroke(1.dp, Color(0xFF0047BB))
            ) {
                Text("Bỏ qua")
            }
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047BB))
            ) {
                Text("Thử lại")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationTrackingScreenPreview() {
    KyTucXaTheme {
        val sampleTracking = TrackingDTO(
            formCode = "REG123456",
            status = TrackingStatusDTO(
                code = "processing",
                label = "Đang xử lý",
                badge = "Quan trọng",
                percent = 60
            ),
            progress = TrackingProgressDTO(
                percent = 60,
                currentStage = "Duyệt hồ sơ",
                userCompleted = 3,
                totalSteps = 5,
                nextAction = "Nộp bản cứng"
            ),
            stages = listOf(
                TrackingStagesDTO(name = "Tiếp nhận hồ sơ", completed = true, percent = 20, type = "step"),
                TrackingStagesDTO(name = "Kiểm tra thông tin", completed = true, percent = 40, type = "step"),
                TrackingStagesDTO(name = "Duyệt hồ sơ", completed = true, percent = 60, type = "step"),
                TrackingStagesDTO(name = "Nộp bản cứng", completed = false, percent = 80, type = "step"),
                TrackingStagesDTO(name = "Hoàn tất", completed = false, percent = 100, type = "step")
            ),
            pendingActions = listOf("Upload đơn có dấu")
        )

        val sampleCurrentResponse = CurrentResponse(
            hasRegistration = true,
            tracking = sampleTracking,
            active = ActiveRegistrationDTO(
                id = "123",
                status = "pending_offline",
                registrationFormCode = "REG123456",
                userId = null,
                submissionType = null,
                source = null,
                formData = null,
                currentStep = null,
                completedSteps = null,
                requiredDocuments = null,
                canSubmitWithoutStamp = null,
                stampedFormDeadline = null,
                signature = null,
                signatureUrl = null,
                isLocked = null,
                isMissingDocuments = null,
                resubmitCount = null,
                submittedAt = null,
                createdAt = null,
                updatedAt = null,
                documents = null,
                missingDocuments = null
            )
        )

        val sampleUiState = RegistrationTrackingUiState(
            isLoading = false,
            currentRegistration = sampleCurrentResponse
        )

        RegistrationTrackingScreen(
            uiState = sampleUiState,
            onUploadStampedForm = {},
            onViewDetail = {},
            onResubmit = {},
            onSkip = {},
            onRefresh = {}
        )
    }
}
