package com.duongnd.kytucxa.feature.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrationSuccessScreen(
    onContinue: () -> Unit
) {
    val primaryColor = Color(0xFF0047BB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Gửi hồ sơ thành công!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yêu cầu đăng ký KTX của bạn đã được tiếp nhận. Ban quản lý sẽ phê duyệt hồ sơ của bạn trong vòng 24h-48h tới.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("TRUY CẬP TRANG CHỦ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
