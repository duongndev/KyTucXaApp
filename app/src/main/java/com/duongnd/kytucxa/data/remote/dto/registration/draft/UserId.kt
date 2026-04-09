package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserId(
    val _id: String,
    val address: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val studentCode: String
)