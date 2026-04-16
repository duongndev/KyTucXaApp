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
        val base64 = bitmap?.let { bitmapToBase64(it) }
        _state.update { 
            it.copy(
                bitmap = bitmap,
                base64 = base64
            )
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun clearSignature() {
        _state.value = SignatureState()
    }
}
