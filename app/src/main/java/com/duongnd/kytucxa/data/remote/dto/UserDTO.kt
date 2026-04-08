package com.duongnd.kytucxa.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    @Json(name = "_id")
    val id: String,
    val fullName: String? = null,
    val email: String,  
    val phoneNumber: String? = null,
    val identityCard: String? = null,
    val gender: String? = null,
    val dateOfBirth: String? = null,
    val role: String,
    val status: String,
    val isEmailVerified: Boolean,
    val isAccountVerified: Boolean,
    val lastLoginAt: String? = null,
    val createdAt: String
)
