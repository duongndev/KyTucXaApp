package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingStagesDTO(
    val name: String?,
    val percent: Int?,
    val completed: Boolean?,
    val type: String?,
)