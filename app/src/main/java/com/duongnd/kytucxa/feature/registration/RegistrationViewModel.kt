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
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val previewRepository: PreviewRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

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
    }

    private fun loadUser() {
        _currentUser.value = sessionManager.getUser()
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
