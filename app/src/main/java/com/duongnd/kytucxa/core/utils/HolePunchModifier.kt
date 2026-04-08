package com.duongnd.kytucxa.core.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Modifier giúp tạo hiệu ứng đục lỗ trên một lớp phủ màu.
 *
 * @param holeRect Vị trí và kích thước của lỗ cần đục.
 * @param cornerRadius Độ bo góc của lỗ (mặc định là 0f - hình chữ nhật sắc nét).
 * @param overlayColor Màu của lớp phủ bên ngoài lỗ (thường là màu đen mờ).
 */
fun Modifier.punchHole(
    holeRect: Rect,
    cornerRadius: Float = 0f,
    overlayColor: Color = Color.Black.copy(alpha = 0.7f)
): Modifier = this.graphicsLayer(alpha = 0.99f) // Quan trọng: Tạo layer riêng để BlendMode.Clear hoạt động
    .drawWithContent {
        // 1. Vẽ nội dung gốc của Composable
        drawContent()

        // 2. Vẽ lớp phủ tối/mờ lên toàn bộ diện tích
        drawRect(color = overlayColor)

        // 3. "Đục lỗ" bằng cách vẽ một hình với BlendMode.Clear
        if (cornerRadius > 0f) {
            drawRoundRect(
                color = Color.Transparent,
                topLeft = holeRect.topLeft,
                size = holeRect.size,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                blendMode = BlendMode.Clear
            )
        } else {
            drawRect(
                color = Color.Transparent,
                topLeft = holeRect.topLeft,
                size = holeRect.size,
                blendMode = BlendMode.Clear
            )
        }
    }
