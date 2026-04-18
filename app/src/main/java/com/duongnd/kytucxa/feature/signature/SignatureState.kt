package com.duongnd.kytucxa.feature.signature

import android.graphics.Bitmap

data class SignatureState(
    val bitmap: Bitmap? = null,
    val base64: String? = null
)
