package com.duongnd.kytucxa.core.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    undoTrigger: Int = 0,
    onSignatureCaptured: (Bitmap?) -> Unit
) {
    var currentPath by remember { mutableStateOf(Path()) }
    val paths = remember { mutableStateListOf<Path>() }
    var lastX by remember { mutableFloatStateOf(0f) }
    var lastY by remember { mutableFloatStateOf(0f) }
    var drawAction by remember { mutableIntStateOf(0) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    // Xử lý hoàn tác (Undo)
    LaunchedEffect(undoTrigger) {
        if (undoTrigger > 0 && paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            drawAction++
            captureBitmap(size, paths, onSignatureCaptured)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .onSizeChanged { size = it }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentPath = Path()
                            currentPath.moveTo(event.x, event.y)
                            lastX = event.x
                            lastY = event.y
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            currentPath.quadraticBezierTo(
                                lastX, lastY,
                                (lastX + event.x) / 2, (lastY + event.y) / 2
                            )
                            lastX = event.x
                            lastY = event.y
                            drawAction++
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            paths.add(currentPath)
                            currentPath = Path()
                            captureBitmap(size, paths, onSignatureCaptured)
                            true
                        }
                        else -> false
                    }
                }
        ) {
            // Vẽ đường kẻ hướng dẫn (Baseline)
            val baselineY = size.height * 0.75f
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(40.dp.toPx(), baselineY),
                end = Offset(size.width.toFloat() - 40.dp.toPx(), baselineY),
                strokeWidth = 1.dp.toPx()
            )

            drawAction // Để trigger recomposition

            // Vẽ các nét đã hoàn thành
            paths.forEach { p ->
                drawPath(
                    path = p,
                    color = Color.Black,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            // Vẽ nét đang vẽ
            drawPath(
                path = currentPath,
                color = Color.Black,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Văn bản hướng dẫn khi chưa có chữ ký
        if (paths.isEmpty() && currentPath.isEmpty) {
            Text(
                text = "Ký tên vào đây",
                color = Color.LightGray.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "X",
                color = Color.LightGray.copy(alpha = 0.4f),
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 44.dp, bottom = (size.height * 0.25f / 2 - 10).coerceAtLeast(0f).dp)
            )
        }
    }
}

private fun captureBitmap(size: IntSize, paths: List<Path>, onCaptured: (Bitmap?) -> Unit) {
    if (size.width <= 0 || size.height <= 0) return
    
    val bitmap = createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    
    if (paths.isEmpty()) {
        onCaptured(null)
    } else {
        paths.forEach { p -> canvas.drawPath(p.asAndroidPath(), paint) }
        onCaptured(bitmap)
    }
}
