package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingProgressDTO(
    val percent: Int?,
    val currentStage: String?,
    val userCompleted: Int?,
    val totalSteps: Int?,
    val nextAction: String?
)