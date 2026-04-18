package com.duongnd.kytucxa.data.remote.dto.registration.current.tracking

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingDTO(
    val formCode: String? = null,
    val status: TrackingStatusDTO? = null,
    val timeline: TrackingTimelineDTO? = null,
    val progress: TrackingProgressDTO? = null,
    val stages: List<TrackingStagesDTO>? = null,
    val stampedForm: StampedFormTrackingDTO? = null,
    val pendingActions: List<String>? = null,
    val flags: TrackingFlagsDTO? = null,
)