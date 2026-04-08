package com.duongnd.kytucxa.feature.registration

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    viewModel: RegistrationViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val primaryColor = Color(0xFF0047BB)
    val secondaryColor = Color(0xFF002B71)
    var isSubmitting by remember { mutableStateOf(false) }

    val idCardFront by viewModel.idCardFront.collectAsState()
    val idCardBack by viewModel.idCardBack.collectAsState()
    val studentCard by viewModel.studentCard.collectAsState()
    val priorityDoc by viewModel.priorityDoc.collectAsState()

    LaunchedEffect(isSubmitting) {
        if (isSubmitting) {
            delay(3000L) // Giả lập thời gian gửi hồ sơ (Sau này thay bằng gọi API)
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hoàn thiện hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSubmitting) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Hồ sơ đính kèm",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                text = "Vui lòng tải lên ảnh chụp các giấy tờ theo quy định để BQL xét duyệt.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Danh sách hồ sơ sử dụng Shared ViewModel
            DocumentUploadItem(
                label = "Căn cước công dân (Mặt trước)",
                icon = Icons.Rounded.Badge,
                uri = idCardFront,
                onUriSelected = { viewModel.updateIdCardFront(it) },
                isDisabled = isSubmitting
            )
            Spacer(modifier = Modifier.height(16.dp))

            DocumentUploadItem(
                label = "Căn cước công dân (Mặt sau)",
                icon = Icons.Rounded.Badge,
                uri = idCardBack,
                onUriSelected = { viewModel.updateIdCardBack(it) },
                isDisabled = isSubmitting
            )
            Spacer(modifier = Modifier.height(16.dp))

            DocumentUploadItem(
                label = "Thẻ sinh viên / Giấy báo nhập học",
                icon = Icons.Rounded.School,
                uri = studentCard,
                onUriSelected = { viewModel.updateStudentCard(it) },
                isDisabled = isSubmitting
            )
            Spacer(modifier = Modifier.height(16.dp))

            DocumentUploadItem(
                label = "Giấy tờ ưu tiên (nếu có)",
                icon = Icons.Rounded.VerifiedUser,
                uri = priorityDoc,
                onUriSelected = { viewModel.updatePriorityDoc(it) },
                isDisabled = isSubmitting
            )

            Spacer(modifier = Modifier.height(48.dp))

            val canSubmit = idCardFront != null && idCardBack != null && studentCard != null

            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSubmitting) {
                    GradientProgressIndicator(
                        colors = listOf(primaryColor, secondaryColor, primaryColor),
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Button(
                        onClick = { isSubmitting = true },
                        modifier = Modifier.fillMaxSize(),
                        enabled = canSubmit,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("GỬI HỒ SƠ ĐĂNG KÝ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "* Lưu ý: Hồ sơ gốc sẽ được nộp trực tiếp khi nhận phòng.",
                fontSize = 12.sp,
                color = Color.Red.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

@Composable
fun DocumentUploadItem(
    label: String,
    icon: ImageVector,
    uri: Uri?,
    onUriSelected: (Uri?) -> Unit,
    isDisabled: Boolean = false
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        if (selectedUri != null) {
            onUriSelected(selectedUri)
        }
    }

    val isUploaded = uri != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUploaded) Color(0xFF10B981).copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = if (isUploaded) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = !isDisabled) {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isUploaded) Icons.Rounded.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (isUploaded) Color(0xFF10B981) else Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isUploaded) "Đã chọn ảnh" else "Chạm để chọn ảnh",
                    color = if (isUploaded) Color(0xFF10B981) else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun GradientProgressIndicator(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Canvas(modifier = modifier.rotate(angle)) {
        drawArc(
            brush = Brush.sweepGradient(colors),
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}
