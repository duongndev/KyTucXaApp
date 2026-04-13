package com.duongnd.kytucxa.feature.registration

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.domain.models.ResidenceRegistrationFields
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    private val _residenceRegistrationFields = MutableStateFlow<ResidenceRegistrationFields?>(null)
    val residenceRegistrationFields: StateFlow<ResidenceRegistrationFields?> = _residenceRegistrationFields.asStateFlow()

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
                        val residence = registration.formData.residence
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
                        val temporary = registration.formData.temporary
                        if (temporary != null) {
                            _residenceRegistrationFields.value = ResidenceRegistrationFields(
                                receiver = temporary.receiver,
                                fullName = temporary.fullName,
                                dob = temporary.dateOfBirth,
                                gender = temporary.gender,
                                idNumber = temporary.cccd,
                                phoneNumber = temporary.phoneNumber,
                                email = temporary.email,
                                ownerName = temporary.ownerName ?: "",
                                ownerRelation = temporary.ownerRelation ?: "",
                                ownerIdNumber = temporary.ownerCccd ?: "",
                                requestContent = temporary.requestContent
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateResidenceRegistrationFields(fields: ResidenceRegistrationFields) {
        _residenceRegistrationFields.value = fields
    }

    fun updateFormFields(fields: FormFields) {
        _formFields.value = fields
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
