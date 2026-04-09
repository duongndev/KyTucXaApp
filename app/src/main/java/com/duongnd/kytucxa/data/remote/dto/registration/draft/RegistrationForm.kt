package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationForm(
    val _id: String,
    val approvedAt: Any,
    val canSubmitWithoutStamp: Boolean,
    val completedSteps: List<Int>,
    val createdAt: String,
    val currentStep: Int,
    val deadline: Any,
    val documents: List<DocumentDraft>,
    val formData: FormData,
    val isLocked: Boolean,
    val isMissingDocuments: Boolean,
    val missingDocuments: List<Any>,
    val registrationFormCode: String,
    val rejectedAt: Any,
    val requiredDocuments: RequiredDocuments,
    val resubmitCount: Int,
    val signature: Any,
    val source: String,
    val status: String,
    val submissionType: String,
    val submittedAt: Any,
    val updatedAt: String,
    val userId: UserId
)