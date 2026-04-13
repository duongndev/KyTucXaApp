package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationForm(
    @Json(name = "_id")
    val id: String?,
    val approvedAt: Any?,
    val canSubmitWithoutStamp: Boolean? = false,
    val completedSteps: List<Int>? = emptyList(),
    val createdAt: String?,
    val currentStep: Int? = 0,
    val deadline: Any?,
    val documents: List<DocumentDraft>? = emptyList(),
    val formData: FormData?,
    val isLocked: Boolean? = false,
    val isMissingDocuments: Boolean? = false,
    val missingDocuments: List<Any>? = emptyList(),
    val registrationFormCode: String?,
    val rejectedAt: Any?,
    val requiredDocuments: RequiredDocuments?,
    val resubmitCount: Int? = 0,
    val signature: Any?,
    val source: String?,
    val status: String?,
    val submissionType: String?,
    val submittedAt: Any?,
    val updatedAt: String?,
    val userId: UserId?
)
