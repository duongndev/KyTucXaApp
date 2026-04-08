package com.duongnd.kytucxa.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun ProfileScreen(
    onNavigateToUpdateProfile: () -> Unit = {}
) {
    val primaryColor = Color(0xFF0061FF)
    val backgroundColor = Color(0xFFF8FAFC)
    
    val scrollState = rememberScrollState()
    
    // State chọn ảnh
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Theo dõi trạng thái cuộn để xử lý dải ngăn cách status bar
    val isScrolled by remember {
        derivedStateOf { scrollState.value > 20 }
    }

    val guardAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "GuardAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 1. Header Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 2. Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            // Digital Resident Card (Thiết kế mới)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(210.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor,
                                Color(0xFF334155)
                            )
                        )
                    )
            ) {
                // Họa tiết trang trí vòng tròn chìm
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.03f),
                        radius = size.maxDimension / 1.5f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KÝ TÚC XÁ",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "THẺ NỘI TRÚ ĐIỆN TỬ",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Nfc,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Section - Click to change photo
                        Surface(
                            modifier = Modifier
                                .size(80.dp)
                                .clickable {
                                    photoPickerLauncher.launch("image/*")
                                },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = "D",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                // Overlay Edit Icon
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CameraAlt,
                                        contentDescription = "Edit photo",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NGUYỄN ĐỨC DƯƠNG",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Badge,
                                    null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MSSV: 20210001",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.MeetingRoom,
                                    null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "P.402 - Khu B",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Status Badge on Card
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "ĐANG NỘI TRÚ",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = Color(0xFF34D399),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sections
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProfileSection(title = "Thông tin chi tiết") {
                    ProfileInfoRow(Icons.Rounded.CalendarToday, "Hợp đồng hiện tại", "01/09/2023 - 30/06/2024")
                    ProfileInfoRow(Icons.Rounded.HomeWork, "Cơ sở lưu trú", "Ký túc xá Khu B")
                    ProfileInfoRow(Icons.Rounded.VerifiedUser, "Xác minh", "Đã xác thực thông tin")
                }

                ProfileSection(title = "Tài khoản & Tiện ích") {
                    ProfileActionRow(Icons.Rounded.Person, "Cập nhật thông tin cá nhân") {
                        onNavigateToUpdateProfile()
                    }
                    ProfileActionRow(Icons.Rounded.History, "Lịch sử ra vào") {}
                    ProfileActionRow(Icons.Rounded.Payment, "Lịch sử thanh toán") {}
                    ProfileActionRow(Icons.Rounded.Settings, "Cài đặt ứng dụng") {}
                    ProfileActionRow(Icons.Rounded.HelpOutline, "Trung tâm hỗ trợ") {}
                }

                // Logout Button
                Button(
                    onClick = { /* Handle Logout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Red
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ĐANG XUẤT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(120.dp))
        }

        // 3. Status Bar Guard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .graphicsLayer { alpha = guardAlpha }
                .background(primaryColor)
        )
    }
}

@Composable
fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1E293B)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0061FF).copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color(0xFF0061FF), modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}

@Composable
fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0061FF).copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color(0xFF0061FF), modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155)
        )
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFFCBD5E1)
        )
    }
}
