package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingStatusDTO(
    val code: String?,
    val label: String?,
    val badge: String?,
    val percent: Int?
)