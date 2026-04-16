package com.duongnd.kytucxa.feature.registration.confirm

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.KTXDialog
import com.duongnd.kytucxa.core.ui.components.StepIndicator
import com.duongnd.kytucxa.feature.signature.SignatureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationConfirmScreen(
    viewModel: RegistrationConfirmViewModel,
    signatureViewModel: SignatureViewModel = hiltViewModel(),
    onViewResidenceDetail: () -> Unit,
    onViewTemporaryDetail: () -> Unit,
    onViewDocumentsDetail: () -> Unit,
    onOpenSignature: () -> Unit,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val signatureState by signatureViewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var isConfirmed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRegistrationData()
    }

    LaunchedEffect(uiState.isSubmitSuccess) {
        if (uiState.isSubmitSuccess) {
            // onSuccess handled via dialog or direct navigation
        }
    }

    BackHandler {
        onBack()
    }

    if (uiState.showSuccessDialog) {
        KTXDialog(
            onDismissRequest = { 
                viewModel.dismissSuccessDialog()
                onSuccess()
            },
            title = "Gửi đơn thành công!",
            description = {
                Text(
                    "Đơn đăng ký của bạn đã được gửi thành công và đang chờ xét duyệt. Bạn có thể theo dõi trạng thái tại mục Lịch sử.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButtonText = "ĐÓNG",
            onConfirm = {
                viewModel.dismissSuccessDialog()
                onSuccess()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Xác nhận thông tin",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                StepIndicator(currentStep = 4)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Vui lòng kiểm tra lại tất cả thông tin dưới đây trước khi gửi đơn đăng ký chính thức.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Hồ sơ đăng ký",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val form = uiState.registrationForm
                    val hasResidence = form?.formData?.residence != null
                    val hasTemporary = form?.formData?.temporary != null
                    val hasDocuments = (form?.documents?.size ?: 0) >= 3

                    ConfirmItemCard(
                        title = "Đơn đăng ký nội trú",
                        isCompleted = hasResidence,
                        icon = Icons.Rounded.Description,
                        onViewDetail = onViewResidenceDetail
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ConfirmItemCard(
                        title = "Đơn đăng ký tạm trú",
                        isCompleted = hasTemporary,
                        icon = Icons.AutoMirrored.Rounded.Assignment,
                        onViewDetail = onViewTemporaryDetail
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ConfirmItemCard(
                        title = "Giấy tờ liên quan",
                        isCompleted = hasDocuments,
                        icon = Icons.Rounded.CloudUpload,
                        onViewDetail = onViewDocumentsDetail
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SignatureSection(
                        signatureBitmap = signatureState.bitmap,
                        onSign = onOpenSignature
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isConfirmed = !isConfirmed }
                            .background(if (isConfirmed) Color(0xFF0047BB).copy(alpha = 0.05f) else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isConfirmed,
                            onCheckedChange = { isConfirmed = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0047BB))
                        )
                        Text(
                            text = "Tôi cam kết những lời khai trong đơn là đúng sự thật, tôi xin chịu trách nhiệm trước pháp luật về các nội dung đã kê khai./.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    KTXButton(
                        text = "GỬI ĐƠN ĐĂNG KÝ",
                        onClick = { 
                            viewModel.submitRegistration(signatureState.base64 ?: "") 
                        },
                        isLoading = uiState.isSubmitting,
                        enabled = isConfirmed && signatureState.bitmap != null && !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ConfirmItemCard(
    title: String,
    isCompleted: Boolean,
    icon: ImageVector,
    onViewDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, if (isCompleted) Color(0xFFE2E8F0) else Color(0xFFFEE2E2))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isCompleted) Color(0xFFF1F5F9) else Color(0xFFFEF2F2),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCompleted) Color(0xFF0047BB) else Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SignatureSection(
    signatureBitmap: Bitmap?,
    onSign: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ký xác nhận",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            if (signatureBitmap != null) {
                Text(
                    text = "Ký lại",
                    color = Color(0xFF0047BB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onSign() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .then(
                    if (signatureBitmap == null) {
                        Modifier
                            .border(
                                BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSign() }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (signatureBitmap != null) {
                Image(
                    bitmap = signatureBitmap.asImageBitmap(),
                    contentDescription = "Chữ ký",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chạm để ký xác nhận",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
