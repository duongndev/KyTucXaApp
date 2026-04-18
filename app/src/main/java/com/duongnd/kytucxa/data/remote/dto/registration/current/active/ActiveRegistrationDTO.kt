package com.duongnd.kytucxa.data.remote.dto.registration.current.active

import com.duongnd.kytucxa.data.remote.dto.registration.FormDataDTO
import com.duongnd.kytucxa.data.remote.dto.registration.RequiredDocumentsDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.UserIdDTO
import com.duongnd.kytucxa.data.remote.dto.registration.document.DocumentDTO
import com.squareup.moshi.Json

data class ActiveRegistrationDTO(
    @Json(name = "_id")
    val id: String?,
    val registrationFormCode: String?,
    val userId: UserIdDTO?,
    val submissionType: String?,
    val source: String?,
    val status: String?,
    val formData: FormDataDTO?,
    val currentStep: Int?,
    val completedSteps: List<Int>?,
    val requiredDocuments: RequiredDocumentsDTO?,
    val canSubmitWithoutStamp: Boolean?,
    val stampedFormDeadline: String?,
    val signature: String?,
    val signatureUrl: String?,
    val isLocked: Boolean?,
    val isMissingDocuments: Boolean?,
    val resubmitCount: Int?,
    val submittedAt: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val documents: List<DocumentDTO>?,
    val missingDocuments: List<Any?>?,
)
