package com.duongnd.kytucxa.feature.registration.confirm

import com.duongnd.kytucxa.data.remote.dto.registration.draft.RegistrationForm

data class RegistrationConfirmState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val registrationForm: RegistrationForm? = null,
    val error: String? = null,
    val isSubmitSuccess: Boolean = false,
    val showSuccessDialog: Boolean = false
)
