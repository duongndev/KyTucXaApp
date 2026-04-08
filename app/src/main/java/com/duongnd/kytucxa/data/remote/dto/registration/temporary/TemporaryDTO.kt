package com.duongnd.kytucxa.data.remote.dto.registration.temporary

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TemporaryDTO(
    val cccd: String,
    val dateOfBirth: String,
    val email: String,
    val fullName: String,
    val gender: String,
    val ownerCccd: String?,
    val ownerName: String?,
    val ownerRelation: String?,
    val phoneNumber: String,
    val `receiver`: String,
    val requestContent: String
)