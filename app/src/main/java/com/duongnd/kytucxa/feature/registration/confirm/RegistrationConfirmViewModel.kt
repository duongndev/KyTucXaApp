package com.duongnd.kytucxa.feature.registration.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationConfirmViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationConfirmState())
    val uiState = _uiState.asStateFlow()

    fun loadRegistrationData() {
        viewModelScope.launch {
            registrationRepository.getCurrentDraft().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                registrationForm = resource.data.registrationForm
                            ) 
                        }
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

    fun submitRegistration(signature: String = "") {
        val formId = _uiState.value.registrationForm?.id ?: return
        
        viewModelScope.launch {
            registrationRepository.submitRegistrationForm(formId, signature).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isSubmitting = true, error = null) }
                    }
                    is Resource.Success -> {
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
