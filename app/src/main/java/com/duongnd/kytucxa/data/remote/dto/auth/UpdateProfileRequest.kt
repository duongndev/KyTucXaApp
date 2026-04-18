package com.duongnd.kytucxa.data.remote.dto.auth

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val identityCard: String? = null,
    val dateOfBirth: Date? = null,
    val gender: String? = null,
    val university: String? = null,
    val studentId: String? = null,
    val major: String? = null,
    val className: String? = null,
    val academicYear: Int? = null
)
