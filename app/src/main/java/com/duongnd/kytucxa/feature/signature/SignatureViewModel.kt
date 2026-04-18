package com.duongnd.kytucxa.feature.signature

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class SignatureViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignatureState())
    val state: StateFlow<SignatureState> = _state.asStateFlow()

    fun updateSignature(bitmap: Bitmap?) {
        val optimizedBitmap = bitmap?.let { trimSignature(it) }
        val base64 = optimizedBitmap?.let { bitmapToBase64(it) }
        _state.update { 
            it.copy(
                bitmap = optimizedBitmap,
                base64 = base64
            )
        }
    }

    private fun trimSignature(bmp: Bitmap): Bitmap {
        var firstX = bmp.width
        var firstY = bmp.height
        var lastX = 0
        var lastY = 0

        // Tìm phạm vi có pixel màu (không trong suốt)
        for (x in 0 until bmp.width) {
            for (y in 0 until bmp.height) {
                val pixel = bmp.getPixel(x, y)
                if (pixel != android.graphics.Color.TRANSPARENT && pixel != 0) {
                    if (x < firstX) firstX = x
                    if (y < firstY) firstY = y
                    if (x > lastX) lastX = x
                    if (y > lastY) lastY = y
                }
            }
        }

        return if (lastX < firstX || lastY < firstY) {
            bmp // Trả về ảnh gốc nếu không tìm thấy nét vẽ
        } else {
            // Thêm một chút padding (8px) để không bị sát mép
            val padding = 8
            val startX = (firstX - padding).coerceAtLeast(0)
            val startY = (firstY - padding).coerceAtLeast(0)
            val width = (lastX - firstX + padding * 2).coerceAtMost(bmp.width - startX)
            val height = (lastY - firstY + padding * 2).coerceAtMost(bmp.height - startY)
            
            Bitmap.createBitmap(bmp, startX, startY, width, height)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // Sử dụng PNG để giữ độ trong suốt cho chữ ký
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun clearSignature() {
        _state.value = SignatureState()
    }
}
