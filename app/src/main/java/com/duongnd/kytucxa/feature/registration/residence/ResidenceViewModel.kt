package com.duongnd.kytucxa.feature.registration.residence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.registration.FormDataDTO
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceDTO
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.domain.models.ResidenceModel
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
class ResidenceViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidenceFormState())
    val uiState: StateFlow<ResidenceFormState> = _uiState.asStateFlow()

    private val _updateResult = MutableStateFlow<Resource<ResidenceResponse>>(Resource.Idle)
    val updateResult: StateFlow<Resource<ResidenceResponse>> = _updateResult.asStateFlow()

    private var existingFormId: String? = null
    private var originalResidence: ResidenceModel? = null
    private var lastFullResponse: ResidenceResponse? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
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

        viewModelScope.launch {
            val cached = registrationRepository.getCachedRegistration()
            if (cached != null && cached.draft != null) {
                Timber.d("ResidenceViewModel: Using cached registration")
                handleLoadedRegistration(cached)
            } else {
                registrationRepository.getCurrentRegistration().collect { resource ->
                    if (resource is Resource.Success) {
                        resource.data?.let { handleLoadedRegistration(it) }
                    }
                }
            }
        }
    }

    private fun handleLoadedRegistration(currentRes: CurrentResponse) {
        val draft = currentRes.draft
        if (draft != null) {
            existingFormId = draft.id

            draft.formData?.residence?.let { resDto ->
                val loadedResidence = ResidenceModel(
                    fullName = resDto.fullName,
                    gender = resDto.gender,
                    dateOfBirth = resDto.dateOfBirth,
                    cccd = resDto.cccd,
                    cccdIdIssueDate = resDto.cccdIdIssueDate,
                    cccdIdIssuePlace = resDto.cccdIdIssuePlace,
                    permanentAddress = resDto.permanentAddress,
                    phoneNumber = resDto.phoneNumber,
                    email = resDto.email,
                    emergencyContact = resDto.emergencyContact,
                    schoolName = resDto.schoolName,
                    academicYear = resDto.academicYear,
                    className = resDto.className,
                    department = resDto.department,
                    major = resDto.major,
                    studentId = resDto.studentId,
                    dormName = resDto.dormName,
                    duration = resDto.duration
                )
                originalResidence = loadedResidence
                _uiState.update { it.copy(residence = loadedResidence) }
            }
        }
    }

    fun updateResidence(update: ResidenceModel.() -> ResidenceModel) {
        _uiState.update { it.copy(residence = it.residence.update()) }
    }

    fun updatePriorityType(priorityType: String) {
        _uiState.update { it.copy(priorityType = priorityType) }
    }

    fun submitForm() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) return

        viewModelScope.launch {
            val model = currentState.residence
            
            if (originalResidence != null && model == originalResidence) {
                Timber.d("Residence data unchanged. Skipping API call.")
                _updateResult.value = Resource.Success(
                    ResidenceResponse(
                        nextStep = 2, 
                        registrationForm = lastFullResponse?.registrationForm ?: dummyForm()
                    )
                )
                return@launch
            }

            _updateResult.value = Resource.Loading
            val formId = existingFormId

            if (formId != null) {
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

                registrationRepository.updateResidenceForm(formId, request).collect { resource ->
                    _updateResult.value = resource
                    if (resource is Resource.Success) {
                        lastFullResponse = resource.data
                        originalResidence = model
                    }
                }
            } else {
                _updateResult.value = Resource.Error("Không thể xác định Form ID")
            }
        }
    }

    private fun dummyForm() = com.duongnd.kytucxa.data.remote.dto.registration.residence.RegistrationForm(
        id = existingFormId ?: "",
        currentStep = 1,
        completedSteps = listOf(1),
        formData = FormDataDTO()
    )

    fun resetUpdateResult() {
        _updateResult.value = Resource.Idle
    }
}
