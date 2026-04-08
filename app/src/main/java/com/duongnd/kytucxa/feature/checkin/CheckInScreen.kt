package com.duongnd.kytucxa.feature.checkin

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckInScreen(onBack: () -> Unit = {}) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Quét mã", "Mã của tôi", "Thẻ KTX")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (selectedTabIndex == 0) Color.Black else Color(0xFFF5F7FA))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (selectedTabIndex == 0) Color.White.copy(alpha = 0.2f) else Color.White
                )
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = if (selectedTabIndex == 0) Color.White else Color.Black
                )
            }

            Text(
                text = "Check-in",
                style = MaterialTheme.typography.titleLarge,
                color = if (selectedTabIndex == 0) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { /* History */ },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (selectedTabIndex == 0) Color.White.copy(alpha = 0.2f) else Color.White
                )
            ) {
                Icon(
                    Icons.Rounded.History,
                    contentDescription = "History",
                    tint = if (selectedTabIndex == 0) Color.White else Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Selector (Custom)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(if (selectedTabIndex == 0) Color.White.copy(alpha = 0.15f) else Color.White)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) Color(0xFF0047BB) else Color.Transparent)
                            .clickable { selectedTabIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else (if (selectedTabIndex == 0) Color.White.copy(alpha = 0.6f) else Color.Gray),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> _root_ide_package_.com.duongnd.kytucxa.feature.checkin.ScannerView()
                1 -> _root_ide_package_.com.duongnd.kytucxa.feature.checkin.MyCodeView()
                2 -> _root_ide_package_.com.duongnd.kytucxa.feature.checkin.VirtualCardView()
            }
        }
    }
}

@Composable
fun VirtualCardView() {
    val infiniteTransition = rememberInfiniteTransition(label = "NFCAnimation")
    val waveScale by infiniteTransition.animateFloat(initialValue = 1f, targetValue = 1.6f, animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "WaveScale")
    val waveAlpha by infiniteTransition.animateFloat(initialValue = 0.4f, targetValue = 0f, animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "WaveAlpha")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Virtual Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0047BB), Color(0xFF002B71))
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KTX STUDENT CARD",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp
                    )
                    Icon(
                        imageVector = Icons.Rounded.Contactless,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "NGUYEN DUC DUONG",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ID: 20210001", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                    Text(text = "ROOM: 402", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // NFC Icon with Animation
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = waveScale; scaleY = waveScale; alpha = waveAlpha }.background(Color(0xFF0047BB), CircleShape))
            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = Color(0xFF0047BB).copy(alpha = 0.15f)) {}
            Icon(imageVector = Icons.Rounded.Nfc, contentDescription = null, tint = Color(0xFF0047BB), modifier = Modifier.size(48.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Sẵn sàng quét", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = "Vui lòng chạm điện thoại vào đầu đọc thẻ\ntại cổng để ra vào ký túc xá", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun ScannerView() {
    Box(modifier = Modifier.fillMaxSize()) {
        _root_ide_package_.com.duongnd.kytucxa.feature.checkin.ScannerOverlay()
        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Đặt mã QR vào trong khung hình", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(24.dp))
            IconButton(onClick = { /* Flash */ }, modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f))) {
                Icon(Icons.Rounded.FlashlightOn, contentDescription = "Flash", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun MyCodeView() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(32.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Nguyễn Đức Dương", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "MSSV: 20210001 - Phòng 402", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
                Box(modifier = Modifier.size(220.dp).background(Color(0xFFF5F7FA), RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Rounded.QrCode2, contentDescription = "My QR Code", modifier = Modifier.fillMaxSize(), tint = Color.Black)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Vui lòng xuất trình mã này\ntại cổng bảo vệ để Check-in", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Mã tự động làm mới sau 30 giây", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
    }
}

@Composable
fun ScannerOverlay() {
    val strokeColor = Color(0xFF0047BB)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width; val height = size.height; val scannerSize = width * 0.7f; val left = (width - scannerSize) / 2; val top = (height - scannerSize) / 2
        drawRect(color = Color.Black.copy(alpha = 0.6f), size = size)
        drawRoundRect(color = Color.Transparent, topLeft = Offset(left, top), size = Size(scannerSize, scannerSize), cornerRadius = CornerRadius(24.dp.toPx()), blendMode = BlendMode.Clear)
        val strokeWidth = 4.dp.toPx(); val cornerSize = 48.dp.toPx()
        drawArc(color = strokeColor, startAngle = 180f, sweepAngle = 90f, useCenter = false, topLeft = Offset(left, top), size = Size(cornerSize, cornerSize), style = Stroke(width = strokeWidth))
        drawArc(color = strokeColor, startAngle = 270f, sweepAngle = 90f, useCenter = false, topLeft = Offset(left + scannerSize - cornerSize, top), size = Size(cornerSize, cornerSize), style = Stroke(width = strokeWidth))
        drawArc(color = strokeColor, startAngle = 90f, sweepAngle = 90f, useCenter = false, topLeft = Offset(left, top + scannerSize - cornerSize), size = Size(cornerSize, cornerSize), style = Stroke(width = strokeWidth))
        drawArc(color = strokeColor, startAngle = 0f, sweepAngle = 90f, useCenter = false, topLeft = Offset(left + scannerSize - cornerSize, top + scannerSize - cornerSize), size = Size(cornerSize, cornerSize), style = Stroke(width = strokeWidth))
    }
}
