package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingFlagsDTO(
    val canUploadStamped: Boolean?,
    val isLocked: Boolean?,
    val hasMissingDocs: Boolean?,
)