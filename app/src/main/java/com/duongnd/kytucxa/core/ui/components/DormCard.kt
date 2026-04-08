package com.duongnd.kytucxa.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.R

@Composable
fun DormCard(
    fullName: String,
    studentId: String,
    university: String,
    room: String,
    expiry: String
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF000000), 
            Color(0xFF1A237E), 
            Color(0xFF311B92), 
            Color(0xFF4A148C), 
            Color(0xFF000000)
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Giới hạn chiều rộng tối đa cho thẻ trên Tablet
        val cardWidthDp = if (maxWidth > 480.dp) 480.dp else maxWidth
        // Tỷ lệ scale dựa trên chiều rộng chuẩn 360dp để điều chỉnh text/padding linh hoạt
        val scaleFactor = (cardWidthDp.value / 360f).coerceIn(0.85f, 1.25f)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Tiêu đề phía trên thẻ cũng được scale theo màn hình
            Text(
                text = "THẺ NỘI TRÚ ĐIỆN TỬ",
                color = Color.Black,
                fontSize = (18 * scaleFactor).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = (12 * scaleFactor).dp),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier
                    .width(cardWidthDp)
                    .aspectRatio(1.58f), // Tỷ lệ chuẩn của thẻ ID
                shape = RoundedCornerShape((24 * scaleFactor).dp),
                elevation = CardDefaults.cardElevation(defaultElevation = (12 * scaleFactor).dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background & Texture Overlay
                    Box(modifier = Modifier.matchParentSize().background(gradient))
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.White.copy(alpha = 0.03f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((20 * scaleFactor).dp)
                    ) {
                        // ===== HEADER SECTION =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "THẺ NỘI TRÚ",
                                    color = Color(0xFFFF4081),
                                    fontSize = (14 * scaleFactor).sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color(0xFFFF4081).copy(alpha = 0.5f),
                                            blurRadius = 10f
                                        )
                                    ),
                                    letterSpacing = (1.2 * scaleFactor).sp
                                )

                                Text(
                                    text = university,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = (10 * scaleFactor).sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // NFC Icon Container
                            Box(
                                modifier = Modifier
                                    .size((36 * scaleFactor).dp)
                                    .clip(RoundedCornerShape((10 * scaleFactor).dp))
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nfc,
                                    contentDescription = null,
                                    tint = Color(0xFFE040FB),
                                    modifier = Modifier.size((20 * scaleFactor).dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1.2f))

                        // ===== MIDDLE SECTION (Avatar & Name) =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Photo
                            Box(
                                modifier = Modifier
                                    .size((65 * scaleFactor).dp, (88 * scaleFactor).dp)
                                    .clip(RoundedCornerShape((12 * scaleFactor).dp))
                                    .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar_3x4),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width((18 * scaleFactor).dp))

                            Column(modifier = Modifier.weight(1f)) {
                                // Auto-resizing Full Name (Always 1 Line)
                                var nameFontSize by remember(fullName, scaleFactor) { 
                                    mutableStateOf((18 * scaleFactor).sp) 
                                }
                                
                                Text(
                                    text = fullName.uppercase(),
                                    color = Color.White,
                                    fontSize = nameFontSize,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    softWrap = false,
                                    onTextLayout = { result ->
                                        if (result.didOverflowWidth && nameFontSize > (10 * scaleFactor).sp) {
                                            nameFontSize *= 0.9f
                                        }
                                    },
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = Color(0xFFE040FB).copy(alpha = 0.3f),
                                            blurRadius = 15f
                                        )
                                    ),
                                    lineHeight = (nameFontSize.value * 1.2).sp
                                )

                                Spacer(modifier = Modifier.height((4 * scaleFactor).dp))

                                // Room Info with Subtle Background
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape((6 * scaleFactor).dp))
                                        .background(Color(0xFFE040FB).copy(alpha = 0.15f))
                                        .padding(horizontal = (8 * scaleFactor).dp, vertical = (2 * scaleFactor).dp)
                                ) {
                                    Text(
                                        text = "PHÒNG: $room",
                                        color = Color(0xFFE040FB),
                                        fontSize = (12 * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // ===== FOOTER SECTION =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoItemAdaptive(title = "MÃ SINH VIÊN", value = studentId, scale = scaleFactor)
                            InfoItemAdaptive(title = "HẾT HẠN", value = expiry, scale = scaleFactor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItemAdaptive(title: String, value: String, scale: Float) {
    Column {
        Text(
            text = title,
            color = Color(0xFFB0BEC5), 
            fontSize = (8 * scale).sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (0.5 * scale).sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = (13 * scale).sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDormCardAdaptive() {
    DormCard(
        fullName = "Trần Thị Hương Giang",
        studentId = "SV20231234",
        university = "Đại học Công nghệ TP.HCM",
        room = "A101",
        expiry = "31/12/2026"
    )
}
