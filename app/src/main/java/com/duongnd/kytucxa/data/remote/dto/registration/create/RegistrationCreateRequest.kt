package com.duongnd.kytucxa.data.remote.dto.registration.create

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationCreateRequest(
    val submissionType: String,
)