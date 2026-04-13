package com.duongnd.kytucxa.data.remote.dto.registration.create

import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.duongnd.kytucxa.data.remote.dto.registration.RequiredDocuments
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse(
    @Json(name = "_id")
    val id: String? = null,
    val completedSteps: List<Any>? = null,
    val createdAt: String? = null,
    val currentStep: Int? = null,
    val formData: FormData? = null,
    val registrationFormCode: String? = null,
    val requiredDocuments: RequiredDocuments? = null,
    val status: String? = null,
    val submissionType: String? = null,
    val updatedAt: String? = null,
    val userId: String? = null,
    
    // Các trường này để chứa dữ liệu khi có đơn cũ đang xử lý
    val existingFormId: String? = null,
    val existingFormCode: String? = null,
    val existingStatus: String? = null
)
