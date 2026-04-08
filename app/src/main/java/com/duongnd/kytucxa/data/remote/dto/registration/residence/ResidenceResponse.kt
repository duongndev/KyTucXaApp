package com.duongnd.kytucxa.data.remote.dto.registration.residence

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResidenceResponse(
    val nextStep: Int,
    val registrationForm: RegistrationForm
)