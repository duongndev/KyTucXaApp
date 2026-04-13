package com.duongnd.kytucxa.feature.registration

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceDTO
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.domain.models.TemporaryModel
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val previewRepository: PreviewRepository,
    private val registrationRepository: RegistrationRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _draft = MutableStateFlow<DraftResponse?>(null)
    val draft: StateFlow<DraftResponse?> = _draft.asStateFlow()

    private val _previewHtml = MutableStateFlow<Resource<String>>(Resource.Idle)
    val previewHtml: StateFlow<Resource<String>> = _previewHtml.asStateFlow()

    fun getPreviewTamTru() {
        viewModelScope.launch {
            _previewHtml.value = Resource.Loading
            previewRepository.getPreviewTamTru("don_tam_tru_KTX").collect { resource ->
                _previewHtml.value = resource
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun resetPreview() {
        _previewHtml.value = Resource.Idle
    }

    // Form data state
    private val _formFields = MutableStateFlow<FormFields?>(null)
    val formFields: StateFlow<FormFields?> = _formFields.asStateFlow()

    private val _temporaryModel = MutableStateFlow<TemporaryModel?>(null)
    val temporaryModel: StateFlow<TemporaryModel?> = _temporaryModel.asStateFlow()

    private val _updateResidenceResult =
        MutableStateFlow<Resource<ResidenceResponse>>(Resource.Idle)
    val updateResidenceResult: StateFlow<Resource<ResidenceResponse>> =
        _updateResidenceResult.asStateFlow()

    // Documents state
    private val _idCardFront = MutableStateFlow<Uri?>(null)
    val idCardFront: StateFlow<Uri?> = _idCardFront.asStateFlow()

    private val _idCardBack = MutableStateFlow<Uri?>(null)
    val idCardBack: StateFlow<Uri?> = _idCardBack.asStateFlow()

    private val _studentCard = MutableStateFlow<Uri?>(null)
    val studentCard: StateFlow<Uri?> = _studentCard.asStateFlow()

    private val _priorityDoc = MutableStateFlow<Uri?>(null)
    val priorityDoc: StateFlow<Uri?> = _priorityDoc.asStateFlow()

    init {
        loadUser()
        loadCurrentDraft()
    }

    private fun loadUser() {
        _currentUser.value = sessionManager.getUser()
    }

    fun loadCurrentDraft() {
        viewModelScope.launch {
            registrationRepository.getCurrentDraft().collect { resource ->
                if (resource is Resource.Success) {
                    _draft.value = resource.data
                    resource.data.registrationForm?.let { registration ->
                        // 1. Map Residence Form Fields
                        val residence = registration.formData?.residence
                        if (residence != null) {
                            _formFields.value = FormFields(
                                fullName = residence.fullName,
                                gender = residence.gender,
                                dob = residence.dateOfBirth,
                                idNumber = residence.cccd,
                                idIssueDate = residence.cccdIdIssueDate,
                                idIssuePlace = residence.cccdIdIssuePlace,
                                permanentAddress = residence.permanentAddress,
                                phoneNumber = residence.phoneNumber,
                                email = residence.email,
                                emergencyContact = residence.emergencyContact,
                                schoolName = residence.schoolName,
                                academicYear = residence.academicYear,
                                className = residence.className,
                                department = residence.department,
                                studentId = residence.studentId,
                                priorityType = "", // Map if available
                                dormName = residence.dormName,
                                duration = residence.duration
                            )
                        }

                        // 2. Map Temporary Residence Fields
                        val temporary = registration.formData?.temporary
                        if (temporary != null) {
                            _temporaryModel.value = TemporaryModel(
                                cccd = temporary.cccd,
                                fullName = temporary.fullName,
                                dateOfBirth = temporary.dateOfBirth,
                                receiver = temporary.receiver,
                                ownerCccd = temporary.ownerCccd,
                                gender = temporary.gender,
                                email = temporary.email,
                                phoneNumber = temporary.phoneNumber,
                                ownerName = temporary.ownerName,
                                ownerRelation = temporary.ownerRelation,
                                requestContent = temporary.requestContent
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateResidenceRegistrationFields(fields: TemporaryModel) {
        _temporaryModel.value = fields
    }

    fun updateFormFields(fields: FormFields) {
        _formFields.value = fields
    }

    fun updateResidenceForm(fields: FormFields) {
        Timber.d("updateResidenceForm called with fields: $fields")
        val formId = _draft.value?.existingFormId
        if (formId == null) {
            Timber.e("updateResidenceForm failed: existingFormId is null. Current draft: ${_draft.value}")
            return
        }
        viewModelScope.launch {
            _updateResidenceResult.value = Resource.Loading
            val request = ResidenceRequest(
                residenceData = ResidenceDTO(
                    academicYear = fields.academicYear,
                    cccd = fields.idNumber,
                    cccdIdIssueDate = fields.idIssueDate,
                    cccdIdIssuePlace = fields.idIssuePlace,
                    className = fields.className,
                    dateOfBirth = fields.dob,
                    department = fields.department,
                    dormName = fields.dormName,
                    duration = fields.duration,
                    email = fields.email,
                    emergencyContact = fields.emergencyContact,
                    fullName = fields.fullName,
                    gender = fields.gender,
                    major = fields.department, // Giả sử major là department nếu không có field riêng
                    permanentAddress = fields.permanentAddress,
                    phoneNumber = fields.phoneNumber,
                    schoolName = fields.schoolName,
                    studentId = fields.studentId
                )
            )
            Timber.d("Sending ResidenceRequest: $request to FormID: $formId")
            registrationRepository.updateResidenceForm(formId, request).collect { resource ->
                _updateResidenceResult.value = resource
                when (resource) {
                    is Resource.Success -> Timber.i("Update Residence success")
                    is Resource.Error -> Timber.e("Update Residence error: ${resource.message}")
                    else -> {}
                }
            }
        }
    }

    fun resetUpdateResidenceResult() {
        _updateResidenceResult.value = Resource.Idle
    }

    fun updateIdCardFront(uri: Uri?) {
        _idCardFront.value = uri
    }

    fun updateIdCardBack(uri: Uri?) {
        _idCardBack.value = uri
    }

    fun updateStudentCard(uri: Uri?) {
        _studentCard.value = uri
    }

    fun updatePriorityDoc(uri: Uri?) {
        _priorityDoc.value = uri
    }
}
