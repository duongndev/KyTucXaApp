package com.duongnd.kytucxa.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentDTO(
    val userId: String?,
    val studentId: String?,
    val university: String?,
    val major: String?,
    val className: String?,
    val academicYear: String?,
    val status: String?,
)
