package com.duongnd.kytucxa.data.remote.dto.registration.current.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DraftResponse(
    val hasDraft: Boolean = false,
    val progressPercent: Int? = null,
    val existingFormId: String? = null,
    val existingFormCode: String? = null,
    val existingStatus: String? = null,
    val registrationForm: RegistrationForm? = null
)