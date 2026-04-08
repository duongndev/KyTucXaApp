package com.duongnd.kytucxa.feature.registration

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import timber.log.Timber
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionMethodScreen(
    onOnlineSelected: () -> Unit,
    onDirectSelected: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf<SubmissionMethod?>(null) }
    val primaryColor = Color(0xFF0047BB)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phương thức nộp hồ sơ", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onExit) {
                        Text("Thoát", color = Color.Red, fontWeight = FontWeight.SemiBold)
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
                .padding(start = 20.dp, end = 20.dp, bottom = 25.dp)
        ) {
            Text(
                text = "Chọn cách thức nộp hồ sơ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Text(
                text = "Bạn muốn nộp hồ sơ đăng ký nội trú theo hình thức nào?",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
            )

            MethodCard(
                title = "Nộp hồ sơ Online",
                description = "Điền đơn điện tử và tải ảnh bản scan giấy tờ lên hệ thống.",
                icon = Icons.Rounded.CloudUpload,
                isSelected = selectedMethod == SubmissionMethod.ONLINE,
                onClick = { selectedMethod = SubmissionMethod.ONLINE },
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            MethodCard(
                title = "Nộp trực tiếp tại văn phòng",
                description = "Tải mẫu đơn, in ra và nộp kèm các giấy tờ tại văn phòng KTX.",
                icon = Icons.Rounded.BusinessCenter,
                isSelected = selectedMethod == SubmissionMethod.DIRECT,
                onClick = { selectedMethod = SubmissionMethod.DIRECT },
                primaryColor = primaryColor
            )

            AnimatedVisibility(
                visible = selectedMethod == SubmissionMethod.DIRECT,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Hồ sơ cần chuẩn bị (Bản cứng):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val requiredDocs = listOf(
                        "Đơn xin nội trú (theo mẫu, có xác nhận của trường)",
                        "02 Ảnh chân dung 3x4 (mới chụp)",
                        "01 Bản photo CCCD (có công chứng)",
                        "01 Bản photo Thẻ sinh viên/Giấy báo nhập học",
                        "Giấy tờ ưu tiên (nếu có)"
                    )

                    requiredDocs.forEach { doc ->
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = doc, fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val downloadUrl = "https://drive.google.com/file/d/1GrRvz-EMlgvRblR6Wq7auHvrIoAN4ory/view?pli=1"
                            try {
                                val request = DownloadManager.Request(downloadUrl.toUri())
                                    .setTitle("Biểu mẫu đơn KTX")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "bieu-mau-don-ktx.doc")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                downloadManager.enqueue(request)
                            } catch (e: Exception) {
                                Timber.tag("SubmissionMethod").e("Download error: ${e.message}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tải xuống mẫu đơn (.doc)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedMethod == SubmissionMethod.ONLINE) onOnlineSelected()
                    else if (selectedMethod == SubmissionMethod.DIRECT) onDirectSelected()
                },
                enabled = selectedMethod != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(
                    text = if (selectedMethod == SubmissionMethod.DIRECT) "XÁC NHẬN NỘP TRỰC TIẾP" else "TIẾP TỤC",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MethodCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) primaryColor.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) primaryColor else Color.LightGray.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) primaryColor else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else primaryColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isSelected) primaryColor else Color(0xFF1E293B)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
            )
        }
    }
}

enum class SubmissionMethod {
    ONLINE, DIRECT
}

@Preview(showBackground = true)
@Composable
fun SubmissionMethodScreenPreview() {
    SubmissionMethodScreen(
        onDirectSelected = {},
        onOnlineSelected = {},
        onExit = {}
    )
}
