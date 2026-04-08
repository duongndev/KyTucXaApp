package com.duongnd.kytucxa.feature.signature

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.kytucxa.core.ui.components.KTXButton
import com.duongnd.kytucxa.core.ui.components.SignaturePad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    onConfirm: (Bitmap) -> Unit,
    onBack: () -> Unit
) {
    var tempSignatureBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var undoTrigger by remember { mutableIntStateOf(0) }
    var signatureKey by remember { mutableIntStateOf(0) }
    
    val primaryColor = Color(0xFF0047BB)

    Scaffold(
        containerColor = Color(0xFFF0F2F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chữ ký điện tử", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Rounded.Close, contentDescription = "Đóng") }
                },
                actions = {
                    Icon(
                        Icons.Rounded.ScreenRotation, 
                        contentDescription = null, 
                        tint = Color.Gray,
                        modifier = Modifier.padding(end = 16.dp).size(20.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mẹo: Xoay ngang điện thoại để ký dễ hơn",
                    fontSize = 12.sp,
                    color = primaryColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                key(signatureKey) {
                    SignaturePad(
                        modifier = Modifier.fillMaxSize(),
                        undoTrigger = undoTrigger,
                        onSignatureCaptured = { tempSignatureBitmap = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { 
                            signatureKey++ 
                            tempSignatureBitmap = null 
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = "Xóa tất cả")
                    }

                    FilledTonalIconButton(
                        onClick = { undoTrigger++ },
                        enabled = tempSignatureBitmap != null
                    ) {
                        Icon(Icons.Rounded.Undo, contentDescription = "Hoàn tác")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Sử dụng KTXButton và điều khiển trạng thái bằng tham số 'enabled'
                    KTXButton(
                        text = "Xác nhận ký",
                        onClick = { tempSignatureBitmap?.let { onConfirm(it) } },
                        modifier = Modifier.width(160.dp),
                        containerColor = primaryColor,
                        enabled = tempSignatureBitmap != null
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignatureScreenPreview() {
    SignatureScreen(onConfirm = {}, onBack = {})
}
