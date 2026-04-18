package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StampedFormTrackingDTO(
    val uploaded: Boolean? = false,
    val deadline: String? = null,
    val daysRemaining: Int? = null,
    val isOverdue: Boolean? = false,
)