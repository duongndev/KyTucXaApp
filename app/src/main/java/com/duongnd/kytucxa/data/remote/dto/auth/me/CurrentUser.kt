package com.duongnd.kytucxa.data.remote.dto.auth.me

import com.duongnd.kytucxa.data.remote.dto.StudentDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
    @Json(name = "_id")
    val id: String? = null,
    val fullName: String? = null,
    val identityCard: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val role: String? = null,
    val status: String? = null,
    val isEmailVerified: Boolean = false,
    val isAccountVerified: Boolean = false,
    val lastLoginAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CurrentUser(
    val user: UserData? = null,
    val student: StudentDTO? = null
)
