package com.duongnd.kytucxa.data.remote.dto.auth.register

import com.duongnd.kytucxa.data.remote.dto.UserDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val user: UserDTO,
    val message: String
)