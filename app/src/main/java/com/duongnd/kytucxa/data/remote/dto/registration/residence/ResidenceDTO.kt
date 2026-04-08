package com.duongnd.kytucxa.data.remote.dto.registration.residence

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResidenceDTO(
    val academicYear: String,
    val cccd: String,
    val cccdIdIssueDate: String,
    val cccdIdIssuePlace: String,
    val className: String,
    val dateOfBirth: String,
    val department: String,
    val dormName: String,
    val duration: String,
    val email: String,
    val emergencyContact: String,
    val fullName: String,
    val gender: String,
    val major: String,
    val permanentAddress: String,
    val phoneNumber: String,
    val schoolName: String,
    val studentId: String
)