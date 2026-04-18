package com.duongnd.kytucxa.feature.registration.submissionMethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateResponse
import com.duongnd.kytucxa.domain.models.SubmissionMethod
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmissionMethodViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubmissionMethodState())
    val state: StateFlow<SubmissionMethodState> = _state.asStateFlow()

    fun onMethodSelected(method: SubmissionMethod) {
        _state.update { it.copy(selectedMethod = method, error = null) }
    }

    fun onContinueClicked() {
        val currentMethod = _state.value.selectedMethod ?: return
        
        if (currentMethod == SubmissionMethod.ONLINE) {
            createRegistration(currentMethod.name.lowercase())
        }
    }

    private fun createRegistration(submissionType: String) {
        viewModelScope.launch {
            registrationRepository.createRegistrationForm(
                RegistrationCreateRequest(submissionType = submissionType)
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is Resource.Error -> {
                        val existingData = result.data as? RegistrationCreateResponse
                        _state.update { 
                            it.copy(
                                isLoading = false, 
                                error = result.message,
                                existingFormId = existingData?.id ?: existingData?.existingFormId,
                                existingFormCode = existingData?.registrationFormCode ?: existingData?.existingFormCode,
                                existingStatus = existingData?.status ?: existingData?.existingStatus
                            ) 
                        }
                    }
                    else -> {
                         _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun resetState() {
        _state.update { 
            it.copy(
                isSuccess = false,
                error = null,
                isLoading = false
            )
        }
    }
}
