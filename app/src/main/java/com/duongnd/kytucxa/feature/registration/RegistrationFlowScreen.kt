package com.duongnd.kytucxa.feature.registration

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class StepStatus {
    COMPLETED, IN_PROGRESS, PENDING, FAILED
}

data class RegistrationStep(
    val title: String,
    val description: String,
    val status: com.duongnd.kytucxa.feature.registration.StepStatus,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFlowScreen(onBack: () -> Unit) {
    val steps = listOf(
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Đăng ký & Điền thông tin",
            "Sinh viên đã hoàn thành điền thông tin cá nhân online.",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED,
            Icons.Rounded.Assignment
        ),
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Upload bản scan giấy tờ",
            "Đã tải lên các giấy tờ cần thiết (CCCD, Giấy báo nhập học...).",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED,
            Icons.Rounded.CloudUpload
        ),
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Admin xét duyệt online",
            "Hồ sơ của bạn đang được cán bộ kiểm tra tính hợp lệ.",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.IN_PROGRESS,
            Icons.Rounded.FactCheck
        ),
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Nộp hồ sơ bản cứng",
            "Sau khi duyệt online, bạn cần nộp bản cứng tại văn phòng KTX.",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.PENDING,
            Icons.Rounded.Description
        ),
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Ký hợp đồng & Đóng tiền",
            "Tạo hợp đồng điện tử và nộp tiền đặt cọc giữ chỗ.",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.PENDING,
            Icons.Rounded.DriveFileRenameOutline
        ),
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStep(
            "Nhận phòng",
            "Phân phòng và hoàn tất thủ tục nhận phòng.",
            _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.PENDING,
            Icons.Rounded.MeetingRoom
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tiến độ đăng ký", fontWeight = FontWeight.Bold) },
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
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0047BB))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.PendingActions,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Trạng thái hiện tại",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            "Đang chờ xét duyệt online",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                "Chi tiết các bước",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                itemsIndexed(steps) { index, step ->
                    _root_ide_package_.com.duongnd.kytucxa.feature.registration.RegistrationStepItem(
                        step = step,
                        isLast = index == steps.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationStepItem(step: com.duongnd.kytucxa.feature.registration.RegistrationStep, isLast: Boolean) {
    val primaryColor = Color(0xFF0047BB)
    val contentColor = when (step.status) {
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED -> primaryColor
        _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.IN_PROGRESS -> primaryColor
        else -> Color.Gray.copy(alpha = 0.3f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline Line & Dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED) primaryColor
                        else if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.IN_PROGRESS) Color.White
                        else Color.White
                    )
                    .border(
                        width = 2.dp,
                        color = if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.PENDING) Color.LightGray else primaryColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED) {
                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Icon(
                        step.icon,
                        null,
                        tint = if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.IN_PROGRESS) primaryColor else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(
                            if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.COMPLETED) primaryColor else Color.LightGray
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .weight(1f)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.PENDING) Color.Gray else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                lineHeight = 18.sp
            )
            
            if (step.status == _root_ide_package_.com.duongnd.kytucxa.feature.registration.StepStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = primaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Đang xử lý",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = primaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
