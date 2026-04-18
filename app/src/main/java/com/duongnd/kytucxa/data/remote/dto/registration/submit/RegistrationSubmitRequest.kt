package com.duongnd.kytucxa.data.remote.dto.registration.submit

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationSubmitRequest(
    val signature: String,
)