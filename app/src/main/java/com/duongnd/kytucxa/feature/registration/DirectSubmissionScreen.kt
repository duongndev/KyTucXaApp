package com.duongnd.kytucxa.feature.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectSubmissionScreen(
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val primaryColor = Color(0xFF0047BB)
    val surfaceColor = Color(0xFFF8FAFC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hướng dẫn nộp trực tiếp", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Rounded.HomeWork,
//                    contentDescription = null,
//                    tint = primaryColor,
//                    modifier = Modifier.size(80.dp)
//                )
//            }

//            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Các bước nộp hồ sơ trực tiếp tại văn phòng Ban quản lý Ký túc xá",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InstructionStep(
                number = "1",
                title = "Hoàn thiện bộ hồ sơ bản cứng",
                description = "Bao gồm đơn xin nội trú (theo mẫu, có xác nhận của trường). 02 ảnh 3x4. 01 bản sao photo CCCD (có công chứng). 01 bản photo Thẻ sinh viên/Giấy báo nhập học và giấy tờ ưu tiên (nếu có)."
            )

            InstructionStep(
                number = "2",
                title = "Đến văn phòng Ban quản lý",
                description = "Địa chỉ: Tầng 1, Tòa nhà A1, Khu ký túc xá tập trung.\nGiờ làm việc: 08:00 - 17:00 (Thứ 2 - Thứ 6)."
            )

            InstructionStep(
                number = "3",
                title = "Xác nhận và nhận biên lai",
                description = "Cán bộ sẽ kiểm tra hồ sơ và cập nhật trạng thái 'Đã nhận hồ sơ' trên ứng dụng cho bạn."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Lưu ý: Bạn cần nộp hồ sơ trong vòng 3 ngày làm việc kể từ khi xác nhận trên ứng dụng để tránh bị hủy yêu cầu giữ chỗ.",
                        fontSize = 13.sp,
                        color = Color(0xFF991B1B)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("TÔI ĐÃ HIỂU VÀ XÁC NHẬN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun InstructionStep(number: String, title: String, description: String) {
    Row(modifier = Modifier.padding(bottom = 20.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFF0047BB), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF334155)
            )
            Text(text = description, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectSubmissionScreenPreview() {
    DirectSubmissionScreen(onConfirm = {}, onBack = {})
}
