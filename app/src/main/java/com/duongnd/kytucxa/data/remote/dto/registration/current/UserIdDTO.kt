package com.duongnd.kytucxa.data.remote.dto.registration.current

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserIdDTO(
    @Json(name = "_id")
    val id: String?,
    val address: String?,
    val email: String?,
    val fullName: String?,
    val phone: String?,
    val studentCode: String?,
)