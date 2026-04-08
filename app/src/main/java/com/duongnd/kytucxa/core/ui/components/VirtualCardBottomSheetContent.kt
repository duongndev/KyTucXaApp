package com.duongnd.kytucxa.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.R

/**
 * Giao diện thẻ nội trú điện tử phong cách "Modern Minimalist Ticket" (Sạch sẽ, Tinh tế & Hiện đại)
 */
@Composable
fun VirtualCardBottomSheetContent() {
    val primaryColor = Color(0xFF10B981) // Emerald 500
    val backgroundColor = Color(0xFFF1F5F9) // Slate 100
    val textColor = Color(0xFF0F172A) // Slate 900

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .size(36.dp, 4.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- TICKET STYLE PASS ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    12.dp,
                    RoundedCornerShape(28.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Upper Section: Logo & Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HỆ THỐNG NỘI TRÚ",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "SMART CAMPUS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    }

                    Surface(
                        color = primaryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Dashed Divider with Cuts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    ) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(40f, 0f),
                            end = Offset(size.width - 40f, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
                            strokeWidth = 2f
                        )
                    }

                    // Left Circular Cut
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-16).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                    )

                    // Right Circular Cut
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(backgroundColor)
                    )
                }

                // Lower Section: User Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_3x4),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier.offset(x = 8.dp, y = 8.dp),
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                Icons.Rounded.Verified,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "THẨM MINH ĐỨC",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "MSSV: 20210001",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailItem("PHÒNG", "A-1206")
                        DetailItem("KHU", "ZONE 1")
                        DetailItem("HẠN DÙNG", "07/2025")
                    }
                }

                // Card Footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(textColor)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.QrCode2,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MÃ ĐỊNH DANH ĐIỆN TỬ",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // NFC Action
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                onClick = { },
                color = Color.White,
                shape = CircleShape,
                shadowElevation = 6.dp,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Nfc,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = primaryColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CHẠM ĐỂ RA VÀO",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            Text(
                text = "Đưa điện thoại lại gần đầu đọc thẻ",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Secondary Action
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(Icons.Rounded.History, contentDescription = null, tint = textColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text("LỊCH SỬ RA VÀO", color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = Color(0xFF0F172A),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VirtualCardPreview() {
    VirtualCardBottomSheetContent()
}

