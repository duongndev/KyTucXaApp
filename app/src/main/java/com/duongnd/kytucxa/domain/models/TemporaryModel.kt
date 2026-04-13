package com.duongnd.kytucxa.domain.models

data class TemporaryModel(
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