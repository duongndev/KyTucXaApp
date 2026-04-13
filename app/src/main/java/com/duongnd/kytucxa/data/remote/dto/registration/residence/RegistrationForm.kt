package com.duongnd.kytucxa.data.remote.dto.registration.residence

import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationForm(
    @Json(name = "_id")
    val id: String,
    val completedSteps: List<Int>,
    val currentStep: Int,
    val formData: FormData
)