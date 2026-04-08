package com.duongnd.kytucxa.data.remote.dto.auth.login

import com.duongnd.kytucxa.data.remote.dto.StudentDTO
import com.duongnd.kytucxa.data.remote.dto.UserDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val tokens: TokenDTO,
    val user: UserDTO,
    val student: StudentDTO? = null
)