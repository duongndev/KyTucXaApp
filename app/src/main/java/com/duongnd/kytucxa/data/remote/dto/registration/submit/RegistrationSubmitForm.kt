package com.duongnd.kytucxa.data.remote.dto.registration.submit

import com.duongnd.kytucxa.data.remote.dto.registration.RequiredDocuments
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationSubmitForm(
    @Json(name = "_id")
    val id: String? = null,
    val completedSteps: List<Any>? = null,
    val currentStep: Int? = null,
    val isLocked: Boolean? = false,
    val registrationFormCode: String? = null,
    val requiredDocuments: RequiredDocuments? = null,
    val signature: String? = null,
    val signatureUrl: String? = null,
    val stampedFormDeadline: String? = null,
    val status: String? = null,
    val submittedAt: String? = null
)