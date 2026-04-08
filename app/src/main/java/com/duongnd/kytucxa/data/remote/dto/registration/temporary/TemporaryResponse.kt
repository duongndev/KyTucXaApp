package com.duongnd.kytucxa.data.remote.dto.registration.temporary

import com.duongnd.kytucxa.data.remote.dto.registration.residence.RegistrationForm
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TemporaryResponse(
    val nextStep: Int,
    val registrationForm: RegistrationForm
)