package com.duongnd.kytucxa.feature.registration

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    fun getPreviewTamTru(formId: String) {
        viewModelScope.launch {
            _previewHtml.value = Resource.Loading
            previewRepository.getPreviewTemporary(formId).collect { resource ->
                _previewHtml.value = resource
            }
        }
    }

    fun getPreviewNoiTru(formId: String) {
        viewModelScope.launch {
            _previewHtml.value = Resource.Loading
            previewRepository.getPreviewResidence(formId).collect { resource ->
                _previewHtml.value = resource
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
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

    private val _isConfirmed = MutableStateFlow(false)
    val isConfirmed: StateFlow<Boolean> = _isConfirmed.asStateFlow()

    init {
        loadUser()
        loadCurrentDraft()
    }

    fun updateSignature(bitmap: Bitmap?) {
        _signatureBitmap.value = bitmap
        _signatureBase64.value = bitmap?.let { bitmapToBase64(it) }
        if (bitmap != null) {
            Timber.d(_signatureBase64.value)
        }
    }

    private val _signatureBitmap = MutableStateFlow<Bitmap?>(null)
    val signatureBitmap: StateFlow<Bitmap?> = _signatureBitmap.asStateFlow()

    private val _signatureBase64 = MutableStateFlow<String?>(null)
    val signatureBase64: StateFlow<String?> = _signatureBase64.asStateFlow()

    fun setConfirmed(confirmed: Boolean) {
        _isConfirmed.value = confirmed
    }

    fun submitRegistration() {
        // Logic gửi đơn tổng hợp lên server
        viewModelScope.launch {
            // ... gọi repository.submitFinalRegistration(...)
        }
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
}
