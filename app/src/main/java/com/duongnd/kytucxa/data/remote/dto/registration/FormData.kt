package com.duongnd.kytucxa.data.remote.dto.registration

import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceDTO
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FormData(
    val residence: ResidenceDTO? = null,
    val temporary: TemporaryDTO? = null
)