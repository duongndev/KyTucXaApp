package com.duongnd.kytucxa.feature.registration.temporary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.registration.FormData
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryDTO
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import com.duongnd.kytucxa.domain.models.TemporaryModel
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TemporaryViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(TemporaryFormState())
    val uiState: StateFlow<TemporaryFormState> = _uiState.asStateFlow()

    private val _updateResult = MutableStateFlow<Resource<TemporaryResponse>>(Resource.Idle)
    val updateResult: StateFlow<Resource<TemporaryResponse>> = _updateResult.asStateFlow()

    private var existingFormId: String? = null
    private var originalTemporary: TemporaryModel? = null
    private var lastFullResponse: TemporaryResponse? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val currentUser = sessionManager.getUser()
        currentUser?.let { profile ->
            _uiState.update { state ->
                state.copy(
                    temporary = state.temporary.copy(
                        fullName = profile.user?.fullName ?: state.temporary.fullName,
                        gender = profile.user?.gender ?: state.temporary.gender,
                        dateOfBirth = profile.user?.dateOfBirth ?: state.temporary.dateOfBirth,
                        cccd = profile.user?.identityCard ?: state.temporary.cccd,
                        phoneNumber = profile.user?.phoneNumber ?: state.temporary.phoneNumber,
                        email = profile.user?.email ?: state.temporary.email,
                    )
                )
            }
        }

        viewModelScope.launch {
            registrationRepository.getCurrentDraft().collect { resource ->
                if (resource is Resource.Success) {
                    val draft = resource.data
                    existingFormId = draft.existingFormId ?: draft.registrationForm?.id

                    draft.registrationForm?.formData?.temporary?.let { resDto ->
                        val loadedTemporary = TemporaryModel(
                            fullName = resDto.fullName ?: "",
                            gender = resDto.gender ?: "",
                            dateOfBirth = resDto.dateOfBirth ?: "",
                            cccd = resDto.cccd ?: "",
                            phoneNumber = resDto.phoneNumber ?: "",
                            email = resDto.email ?: "",
                            receiver = resDto.receiver ?: "",
                            ownerCccd = resDto.ownerCccd ?: "",
                            ownerName = resDto.ownerName ?: "",
                            ownerRelation = resDto.ownerRelation ?: "",
                            requestContent = resDto.requestContent ?: ""
                        )
                        originalTemporary = loadedTemporary
                        _uiState.update { state ->
                            state.copy(temporary = loadedTemporary)
                        }
                    }
                }
            }
        }
    }

    fun updateTemporary(update: TemporaryModel.() -> TemporaryModel) {
        _uiState.update { it.copy(temporary = it.temporary.update()) }
    }

    fun submitForm() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) return

        viewModelScope.launch {
            val model = currentState.temporary
            
            if (originalTemporary != null && model == originalTemporary) {
                Timber.d("Temporary data unchanged. Skipping API call.")
                _updateResult.value = Resource.Success(
                    TemporaryResponse(
                        nextStep = 3, 
                        registrationForm = lastFullResponse?.registrationForm ?: dummyForm()
                    )
                )
                return@launch
            }

            _updateResult.value = Resource.Loading
            val formId = existingFormId

            if (formId != null) {
                val request = TemporaryRequest(
                    temporaryData = TemporaryDTO(
                        cccd = model.cccd,
                        dateOfBirth = model.dateOfBirth,
                        email = model.email,
                        fullName = model.fullName,
                        gender = model.gender,
                        ownerCccd = model.ownerCccd,
                        ownerName = model.ownerName,
                        ownerRelation = model.ownerRelation,
                        phoneNumber = model.phoneNumber,
                        receiver = model.receiver,
                        requestContent = model.requestContent
                    )
                )

                registrationRepository.updateTemporaryForm(formId, request).collect { resource ->
                    _updateResult.value = resource
                    if (resource is Resource.Success) {
                        lastFullResponse = resource.data
                        originalTemporary = model
                    }
                }
            } else {
                _updateResult.value = Resource.Error("Không thể xác định Form ID")
            }
        }
    }

    private fun dummyForm() = com.duongnd.kytucxa.data.remote.dto.registration.residence.RegistrationForm(
        id = existingFormId ?: "",
        currentStep = 2,
        completedSteps = listOf(1, 2),
        formData = FormData()
    )

    fun resetUpdateResult() {
        _updateResult.value = Resource.Idle
    }
}
