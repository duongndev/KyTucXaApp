package com.duongnd.kytucxa.feature.auth.register

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.duongnd.kytucxa.core.ui.components.KTXButton
import java.io.File
import java.io.FileOutputStream

@Composable
fun AvatarUploadScreen(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF0047BB)

    // State chọn ảnh và cắt ảnh
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var rawImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCropDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            rawImageUri = uri
            showCropDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Text("Quay lại", color = Color.White)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "ẢNH THẺ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = "Vui lòng chọn ảnh chân dung hoặc ảnh thẻ 3x4 \nđể làm thẻ ký túc xá",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Avatar Placeholder (Hiển thị theo tỉ lệ 3:4)
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable {
                        photoPickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.AddAPhoto,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chọn ảnh", color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KTXButton(
                text = "TIẾP THEO",
                onClick = {
                    if (selectedImageUri != null) {
                        onNext()
                    } else {
                        Toast.makeText(context, "Vui lòng chọn ảnh thẻ trước khi tiếp tục", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                enabled = selectedImageUri != null
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showCropDialog && rawImageUri != null) {
        ImageCropDialog(
            uri = rawImageUri!!,
            onConfirm = { croppedUri ->
                selectedImageUri = croppedUri
                showCropDialog = false
            },
            onDismiss = { showCropDialog = false }
        )
    }
}

@Composable
fun ImageCropDialog(
    uri: Uri,
    onConfirm: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Tải bitmap dưới dạng Android Bitmap để xử lý cắt chính xác
    val androidBitmap = remember(uri) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    if (androidBitmap == null) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    val imageBitmap = remember(androidBitmap) { androidBitmap.asImageBitmap() }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewWidth = constraints.maxWidth.toFloat()
                val viewHeight = constraints.maxHeight.toFloat()

                // Kích thước khung 3:4 (chiếm 80% chiều rộng màn hình)
                val frameWidth = viewWidth * 0.8f
                val frameHeight = frameWidth * (4f / 3f)
                val frameLeft = (viewWidth - frameWidth) / 2
                val frameTop = (viewHeight - frameHeight) / 2

                // Tính toán tỷ lệ cơ bản để ảnh vừa khít màn hình ban đầu
                val imgWidth = androidBitmap.width.toFloat()
                val imgHeight = androidBitmap.height.toFloat()
                val baseScale = minOf(viewWidth / imgWidth, viewHeight / imgHeight)

                // Layer hiển thị ảnh
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = baseScale * scale,
                                scaleY = baseScale * scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }

                // Layer Overlay: Hiệu ứng đục lỗ khung 3:4 (trong suốt bên trong, mờ bên ngoài)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.99f) // Cần thiết để BlendMode.Clear hoạt động chính xác
                ) {
                    val rect = Rect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight)
                    val cornerRadiusPx = 12.dp.toPx()

                    // 1. Vẽ lớp phủ tối (Overlay) lên toàn màn hình
                    drawRect(color = Color.Black.copy(alpha = 0.7f))

                    // 2. "Đục lỗ" (Punch hole) bằng BlendMode.Clear giúp vùng trong khung hoàn toàn trong suốt và sắc nét
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        blendMode = BlendMode.Clear
                    )

                    // 3. Vẽ viền trắng sắc nét để làm nổi bật khung cắt
                    drawRoundRect(
                        color = Color.White,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                // Các nút điều khiển
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.White)
                    }

                    Button(
                        onClick = {
                            // Logic cắt ảnh chính xác dựa trên tọa độ hiển thị
                            val visualWidth = imgWidth * baseScale * scale
                            val visualHeight = imgHeight * baseScale * scale
                            
                            val centerX = viewWidth / 2
                            val centerY = viewHeight / 2
                            
                            val imgLeft = centerX + offset.x - visualWidth / 2
                            val imgTop = centerY + offset.y - visualHeight / 2
                            
                            // Chuyển đổi tọa độ từ màn hình sang tọa độ bitmap gốc
                            val cropX = (frameLeft - imgLeft) / (baseScale * scale)
                            val cropY = (frameTop - imgTop) / (baseScale * scale)
                            val cropW = frameWidth / (baseScale * scale)
                            val cropH = frameHeight / (baseScale * scale)
                            
                            try {
                                val x = cropX.toInt().coerceIn(0, androidBitmap.width - 1)
                                val y = cropY.toInt().coerceIn(0, androidBitmap.height - 1)
                                val width = cropW.toInt().coerceAtMost(androidBitmap.width - x)
                                val height = cropH.toInt().coerceAtMost(androidBitmap.height - y)

                                if (width > 0 && height > 0) {
                                    val croppedBitmap = Bitmap.createBitmap(androidBitmap, x, y, width, height)
                                    val file = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
                                    FileOutputStream(file).use { out ->
                                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                    }
                                    onConfirm(Uri.fromFile(file))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0047BB)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xong", fontWeight = FontWeight.Bold)
                    }
                }
                
                Text(
                    "Dùng 2 ngón tay để thu phóng và di chuyển ảnh vào khung",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 60.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
