package com.duongnd.kytucxa.data.remote.dto.registration.residence

import com.duongnd.kytucxa.data.remote.dto.registration.FormData

data class RegistrationForm(
    val _id: String,
    val completedSteps: List<Int>,
    val currentStep: Int,
    val formData: FormData
)