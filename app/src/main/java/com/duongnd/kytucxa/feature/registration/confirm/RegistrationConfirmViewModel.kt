package com.duongnd.kytucxa.feature.registration.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.RegistrationForm
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegistrationConfirmViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationConfirmState())
    val uiState = _uiState.asStateFlow()

    fun loadRegistrationData() {
        viewModelScope.launch {
            val cached = registrationRepository.getCachedRegistration()
            if (cached != null && cached.draft != null) {
                Timber.d("RegistrationConfirmViewModel: Using cached registration")
                handleLoadedRegistration(cached)
            } else {
                registrationRepository.getCurrentRegistration().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }

                        is Resource.Success -> {
                            resource.data?.let { handleLoadedRegistration(it) }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = resource.message)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun handleLoadedRegistration(currentRes: CurrentResponse) {
        val draftDto = currentRes.draft
        val form = if (draftDto != null) {
            RegistrationForm(
                id = draftDto.id,
                registrationFormCode = draftDto.registrationFormCode,
                formData = draftDto.formData,
                currentStep = draftDto.currentStep ?: 0,
                completedSteps = draftDto.completedSteps ?: emptyList(),
                status = draftDto.status,
                approvedAt = null,
                canSubmitWithoutStamp = draftDto.canSubmitWithoutStamp,
                createdAt = draftDto.createdAt,
                deadline = null,
                documents = emptyList(),
                isLocked = draftDto.isLocked,
                isMissingDocuments = draftDto.isMissingDocuments,
                missingDocuments = emptyList(),
                rejectedAt = null,
                requiredDocuments = draftDto.requiredDocuments,
                resubmitCount = draftDto.resubmitCount ?: 0,
                signature = null,
                source = draftDto.source,
                submissionType = draftDto.submissionType,
                submittedAt = null,
                updatedAt = draftDto.updatedAt,
                userId = draftDto.userId
            )
        } else null

        _uiState.update {
            it.copy(
                isLoading = false,
                registrationForm = form
            )
        }
    }

    fun submitRegistration(signature: String = "") {
        val formId = _uiState.value.registrationForm?.id ?: return
        
        viewModelScope.launch {
            registrationRepository.submitRegistrationForm(formId, signature).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isSubmitting = true, error = null) }
                    }
                    is Resource.Success -> {
                        registrationRepository.clearCachedRegistration()
                        _uiState.update { 
                            it.copy(
                                isSubmitting = false, 
                                isSubmitSuccess = true,
                                showSuccessDialog = true
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(isSubmitting = false, error = resource.message) 
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(isSubmitting = false) }
                    }
                }
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }
}
