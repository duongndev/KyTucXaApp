package com.duongnd.kytucxa.feature.registration

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.StepIndicator
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationConfirmScreen(
    viewModel: RegistrationViewModel,
    onViewResidenceDetail: () -> Unit,
    onViewTemporaryDetail: () -> Unit,
    onViewDocumentsDetail: () -> Unit,
    onOpenSignature: () -> Unit,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val signatureBitmap by viewModel.signatureBitmap.collectAsState()
    val isConfirmed by viewModel.isConfirmed.collectAsState()
    
    // Thu thập dữ liệu để hiển thị tóm tắt
    val residenceFields by viewModel.formFields.collectAsState()
    val temporaryFields by viewModel.temporaryModel.collectAsState()
    val idFront by viewModel.idCardFront.collectAsState()
    val idBack by viewModel.idCardBack.collectAsState()
    val studentCard by viewModel.studentCard.collectAsState()
    
    val scrollState = rememberScrollState()

    // Handle System Back Press
    BackHandler {
        onBack()
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
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
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
                // Header Summary
                Text(
                    text = "Vui lòng kiểm tra lại tất cả thông tin dưới đây trước khi gửi đơn đăng ký chính thức.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Section: Form Details
                Text(
                    text = "Hồ sơ đăng ký",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ConfirmItemCard(
                    title = "Đơn đăng ký nội trú",
                    isCompleted = residenceFields != null,
                    icon = Icons.Rounded.Description,
                    onViewDetail = onViewResidenceDetail
                )

                Spacer(modifier = Modifier.height(12.dp))

                ConfirmItemCard(
                    title = "Đơn đăng ký tạm trú",
                    isCompleted = temporaryFields != null,
                    icon = Icons.AutoMirrored.Rounded.Assignment,
                    onViewDetail = onViewTemporaryDetail
                )

                Spacer(modifier = Modifier.height(12.dp))

                val docCount = listOfNotNull(idFront, idBack, studentCard).size
                ConfirmItemCard(
                    title = "Giấy tờ liên quan",
                    isCompleted = docCount >= 3,
                    icon = Icons.Rounded.CloudUpload,
                    onViewDetail = onViewDocumentsDetail
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Signature
                SignatureSection(
                    signatureBitmap = signatureBitmap,
                    onSign = onOpenSignature
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox Confirmation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setConfirmed(!isConfirmed) }
                        .background(if (isConfirmed) Color(0xFF0047BB).copy(alpha = 0.05f) else Color.Transparent)
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = isConfirmed,
                        onCheckedChange = { viewModel.setConfirmed(it) },
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

                // Submit Button
                KTXButton(
                    text = "GỬI ĐƠN ĐĂNG KÝ",
                    onClick = {
                        viewModel.submitRegistration()
                        onSuccess()
                    },
                    enabled = isConfirmed && signatureBitmap != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
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
                Timber.d("Displaying signature bitmap with size: ${signatureBitmap.width}x${signatureBitmap.height}")
                Timber.d("Signature: $signatureBitmap")
                Image(
                    bitmap = signatureBitmap.asImageBitmap(),
                    contentDescription = "Signature",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.Draw,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ký ở phần này",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
