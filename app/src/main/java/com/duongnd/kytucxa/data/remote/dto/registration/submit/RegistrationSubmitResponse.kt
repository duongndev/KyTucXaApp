package com.duongnd.kytucxa.data.remote.dto.registration.submit

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationSubmitResponse(
    val message: String? = null,
    val needStampedForm: Boolean? = false,
    val registrationForm: RegistrationSubmitForm? = null,
    val stampedFormDeadline: String? = null
)