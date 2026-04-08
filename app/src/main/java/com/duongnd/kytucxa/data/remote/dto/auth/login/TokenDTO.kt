package com.duongnd.kytucxa.data.remote.dto.auth.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenDTO(
    val accessToken: String,
    val refreshToken: String
)
