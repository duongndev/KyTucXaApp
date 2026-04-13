package com.duongnd.kytucxa.feature.registration.residence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceDTO
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.domain.models.ResidenceModel
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationRequest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResidenceViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidenceFormState())
    val uiState: StateFlow<ResidenceFormState> = _uiState.asStateFlow()

    private val _updateResult = MutableStateFlow<Resource<ResidenceResponse>>(Resource.Idle)
    val updateResult: StateFlow<Resource<ResidenceResponse>> = _updateResult.asStateFlow()

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
                    residence = state.residence.copy(
                        fullName = profile.user?.fullName ?: state.residence.fullName,
                        gender = profile.user?.gender ?: state.residence.gender,
                        dateOfBirth = profile.user?.dateOfBirth ?: state.residence.dateOfBirth,
                        cccd = profile.user?.identityCard ?: state.residence.cccd,
                        phoneNumber = profile.user?.phoneNumber ?: state.residence.phoneNumber,
                        email = profile.user?.email ?: state.residence.email,
                        schoolName = profile.student?.university ?: state.residence.schoolName,
                        academicYear = profile.student?.academicYear ?: state.residence.academicYear,
                        className = profile.student?.className ?: state.residence.className,
                        department = profile.student?.major ?: state.residence.department,
                        major = profile.student?.major ?: state.residence.major,
                        studentId = profile.student?.studentId ?: state.residence.studentId
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

                    draft.registrationForm?.formData?.residence?.let { resDto ->
                        _uiState.update { state ->
                            state.copy(
                                residence = state.residence.copy(
                                    fullName = resDto.fullName ?: state.residence.fullName,
                                    gender = resDto.gender ?: state.residence.gender,
                                    dateOfBirth = resDto.dateOfBirth ?: state.residence.dateOfBirth,
                                    cccd = resDto.cccd ?: state.residence.cccd,
                                    cccdIdIssueDate = resDto.cccdIdIssueDate ?: state.residence.cccdIdIssueDate,
                                    cccdIdIssuePlace = resDto.cccdIdIssuePlace ?: state.residence.cccdIdIssuePlace,
                                    permanentAddress = resDto.permanentAddress ?: state.residence.permanentAddress,
                                    phoneNumber = resDto.phoneNumber ?: state.residence.phoneNumber,
                                    email = resDto.email ?: state.residence.email,
                                    emergencyContact = resDto.emergencyContact ?: state.residence.emergencyContact,
                                    schoolName = resDto.schoolName ?: state.residence.schoolName,
                                    academicYear = resDto.academicYear ?: state.residence.academicYear,
                                    className = resDto.className ?: state.residence.className,
                                    department = resDto.department ?: state.residence.department,
                                    major = resDto.major ?: state.residence.major,
                                    studentId = resDto.studentId ?: state.residence.studentId,
                                    dormName = resDto.dormName ?: state.residence.dormName,
                                    duration = resDto.duration ?: state.residence.duration
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Cập nhật thông tin ResidenceModel
     */
    fun updateResidence(update: ResidenceModel.() -> ResidenceModel) {
        _uiState.update { it.copy(residence = it.residence.update()) }
    }

    fun updatePriorityType(value: String) {
        _uiState.update { it.copy(priorityType = value) }
    }

    fun updateSignature(bitmap: android.graphics.Bitmap?) {
        _uiState.update { it.copy(signatureBitmap = bitmap) }
    }

    /**
     * Submit form dựa trên cấu trúc của RegistrationViewModel.updateResidenceForm
     */
    fun submitForm() {
        val currentState = _uiState.value
        Timber.d("Submit button clicked. Current Residence: ${currentState.residence}")
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
                val model = currentState.residence
                val request = ResidenceRequest(
                    residenceData = ResidenceDTO(
                        academicYear = model.academicYear,
                        cccd = model.cccd,
                        cccdIdIssueDate = model.cccdIdIssueDate,
                        cccdIdIssuePlace = model.cccdIdIssuePlace,
                        className = model.className,
                        dateOfBirth = model.dateOfBirth,
                        department = model.department,
                        dormName = model.dormName,
                        duration = model.duration,
                        email = model.email,
                        emergencyContact = model.emergencyContact,
                        fullName = model.fullName,
                        gender = model.gender,
                        major = model.major,
                        permanentAddress = model.permanentAddress,
                        phoneNumber = model.phoneNumber,
                        schoolName = model.schoolName,
                        studentId = model.studentId
                    )
                )

                Timber.d("Updating Residence Form for ID: $formId")
                registrationRepository.updateResidenceForm(formId, request).collect { resource ->
                    _updateResult.value = resource
                    if (resource is Resource.Success) {
                        Timber.i("Update Residence Form successful")
                    } else if (resource is Resource.Error) {
                        Timber.e("Update Residence Form failed: ${resource.message}")
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
