package com.duongnd.kytucxa.data.remote.dto.auth.verify

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)