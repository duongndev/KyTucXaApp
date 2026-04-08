package com.duongnd.kytucxa.data.remote.dto.auth.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)