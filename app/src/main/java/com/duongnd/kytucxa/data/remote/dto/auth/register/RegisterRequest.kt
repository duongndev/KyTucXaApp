package com.duongnd.kytucxa.data.remote.dto.auth.register

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val confirmPassword: String,
    val email: String,
    val fullName: String,
    val password: String,
)