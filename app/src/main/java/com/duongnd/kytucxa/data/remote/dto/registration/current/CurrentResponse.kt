package com.duongnd.kytucxa.data.remote.dto.registration.current

import com.duongnd.kytucxa.data.remote.dto.registration.current.active.ActiveRegistrationDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftRegistrationDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.tracking.TrackingDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentResponse(
    val hasRegistration: Boolean = false,
    val type: String? = null,
    val progressPercent: Int? = null,
    val draft: DraftRegistrationDTO? = null,
    val active: ActiveRegistrationDTO? = null,
    val tracking: TrackingDTO? = null
)