package com.duongnd.kytucxa.data.remote.dto.registration.current.draft

import com.duongnd.kytucxa.data.remote.dto.registration.FormDataDTO
import com.duongnd.kytucxa.data.remote.dto.registration.RequiredDocumentsDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.UserIdDTO
import com.duongnd.kytucxa.data.remote.dto.registration.document.DocumentDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DraftRegistrationDTO(
    @Json(name = "_id")
    val id: String? = null,
    val registrationFormCode: String? = null,
    val userId: UserIdDTO? = null,
    val submissionType: String? = null,
    val source: String? = null,
    val status: String? = null,
    val formData: FormDataDTO? = null,
    val currentStep: Int? = null,
    val completedSteps: List<Int>? = null,
    val requiredDocuments: RequiredDocumentsDTO? = null,
    val canSubmitWithoutStamp: Boolean? = null,
    val isLocked: Boolean? = null,
    val isMissingDocuments: Boolean? = null,
    val resubmitCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val documents: List<DocumentDTO>? = null,
    val missingDocuments: List<Any?>? = null,
)