package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DraftResponse(
    val hasDraft: Boolean = false,
    val progressPercent: Int? = null,
    val registrationForm: RegistrationForm? = null
)