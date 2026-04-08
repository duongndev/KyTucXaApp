package com.duongnd.kytucxa.data.remote.dto.registration.temporary

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TemporaryRequest(
    val temporaryData: TemporaryDTO
)