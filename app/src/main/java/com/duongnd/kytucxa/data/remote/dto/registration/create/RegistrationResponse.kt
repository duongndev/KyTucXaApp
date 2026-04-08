package com.duongnd.kytucxa.data.remote.dto.registration.create

import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.duongnd.kytucxa.data.remote.dto.registration.RequiredDocuments
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse(
    val _id: String,
    val completedSteps: List<Any>,
    val createdAt: String,
    val currentStep: Int,
    val formData: FormData,
    val registrationFormCode: String,
    val requiredDocuments: RequiredDocuments,
    val status: String,
    val submissionType: String,
    val updatedAt: String,
    val userId: String
)