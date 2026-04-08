package com.duongnd.kytucxa.data.remote.dto.registration.residence

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResidenceRequest(
    val residenceData: ResidenceDTO
)