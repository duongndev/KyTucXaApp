package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingTimelineDTO(
    val submittedAt: String?,
    val lastUpdated: String?,
)