package com.duongnd.kytucxa.feature.registration.temporary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
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


    init {
        loadInitialData()
    }


    private fun loadInitialData() {
        // Ưu tiên 1: Load từ thông tin cá nhân trong Session
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

        // Ưu tiên 2: Ghi đè bằng bản nháp (Draft) trên server nếu có
        viewModelScope.launch {
            registrationRepository.getCurrentDraft().collect { resource ->
                if (resource is Resource.Success) {
                    val draft = resource.data
                    // Lấy ID từ existingFormId hoặc từ chính registrationForm
                    existingFormId = draft.existingFormId ?: draft.registrationForm?.id
                    Timber.d("Loaded Draft. existingFormId: $existingFormId")

                    draft.registrationForm?.formData?.temporary?.let { resDto ->
                        _uiState.update { state ->
                            state.copy(
                                temporary = state.temporary.copy(
                                    fullName = resDto.fullName ?: state.temporary.fullName,
                                    gender = resDto.gender ?: state.temporary.gender,
                                    dateOfBirth = resDto.dateOfBirth ?: state.temporary.dateOfBirth,
                                    cccd = resDto.cccd ?: state.temporary.cccd,
                                    phoneNumber = resDto.phoneNumber ?: state.temporary.phoneNumber,
                                    email = resDto.email ?: state.temporary.email,
                                    receiver = resDto.receiver ?: state.temporary.receiver,
                                    ownerCccd = resDto.ownerCccd ?: state.temporary.ownerCccd,
                                    ownerName = resDto.ownerName ?: state.temporary.ownerName,
                                    ownerRelation = resDto.ownerRelation
                                        ?: state.temporary.ownerRelation,
                                    requestContent = resDto.requestContent
                                        ?: state.temporary.requestContent
                                )
                            )
                        }
                    }
                }
            }
        }
    }


    /**
     * Cập nhật thông tin TemporaryModel
     */
    fun updateTemporary(update: TemporaryModel.() -> TemporaryModel) {
        _uiState.update { it.copy(temporary = it.temporary.update()) }
    }

    fun updateSignature(bitmap: android.graphics.Bitmap?) {
        _uiState.update { it.copy(signatureBitmap = bitmap) }
    }


    fun submitForm() {
        val currentState = _uiState.value
        Timber.d("Submit button clicked. Current Residence: ${currentState.temporary}")
        Timber.d("Is form valid: ${currentState.isFormValid}")

        if (!currentState.isFormValid) {
            Timber.w("Form is invalid. Cannot submit.")
            return
        }

        viewModelScope.launch {
            _updateResult.value = Resource.Loading
            val formId = existingFormId

            // Tiến hành cập nhật dữ liệu form
            if (formId != null) {
                val model = currentState.temporary
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


                Timber.d("Updating Temporary Form for ID: $formId")
                registrationRepository.updateTemporaryForm(formId, request).collect { resource ->
                    _updateResult.value = resource
                    if (resource is Resource.Success) {
                        Timber.i("Update Temporary Form successful")
                    } else if (resource is Resource.Error) {
                        Timber.e("Update Temporary Form failed: ${resource.message}")
                    }
                }
            } else {
                _updateResult.value = Resource.Error("Không thể xác định Form ID")
            }
        }

    }

    fun resetUpdateResult() {
        _updateResult.value = Resource.Idle
    }
}