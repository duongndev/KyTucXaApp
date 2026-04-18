package com.duongnd.kytucxa.feature.registration.tracking

import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse

data class RegistrationTrackingUiState(
    val isLoading: Boolean = false,
    val currentRegistration: CurrentResponse? = null,
    val error: String? = null
)
