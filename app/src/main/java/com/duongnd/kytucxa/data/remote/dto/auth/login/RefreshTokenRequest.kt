package com.duongnd.kytucxa.data.remote.dto.auth.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    val refreshToken: String
)
