package com.duongnd.kytucxa.feature.registration.uploadDocument

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UploadDocumentViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadDocumentState())
    val uiState = _uiState.asStateFlow()

    fun onIdCardFrontChanged(formId: String, uri: Uri?) {
        _uiState.update { it.copy(idCardFront = uri) }
        uri?.let { uploadSingleDocument(formId, it, "cccd_front", "Mặt trước CCCD") }
    }

    fun onIdCardBackChanged(formId: String, uri: Uri?) {
        _uiState.update { it.copy(idCardBack = uri) }
        uri?.let { uploadSingleDocument(formId, it, "cccd_back", "Mặt sau CCCD") }
    }

    fun onStudentCardChanged(formId: String, uri: Uri?) {
        _uiState.update { it.copy(studentCard = uri) }
        uri?.let { uploadSingleDocument(formId, it, "student_card", "Thẻ sinh viên") }
    }

    fun onPriorityDocChanged(formId: String, uri: Uri?) {
        _uiState.update { it.copy(priorityDoc = uri) }
        uri?.let { uploadSingleDocument(formId, it, "priority_proof", "Giấy tờ ưu tiên") }
    }

    private fun uploadSingleDocument(
        formId: String,
        uri: Uri,
        type: String,
        note: String
    ) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    uploadResult = Resource.Loading,
                    uploadingField = type
                ) 
            }

            try {
                val imagePart = uriToMultipart(uri, "image")
                if (imagePart == null) {
                    _uiState.update { it.copy(uploadResult = Resource.Error("Không thể xử lý ảnh")) }
                    return@launch
                }

                registrationRepository.updateDocumentForm(
                    formId = formId,
                    image = imagePart,
                    type = type,
                    note = note
                ).collect { result ->
                    _uiState.update { it.copy(uploadResult = result) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(uploadResult = Resource.Error(e.message ?: "Lỗi upload")) 
                }
            } finally {
                _uiState.update { it.copy(uploadingField = null) }
            }
        }
    }

    private fun uriToMultipart(uri: Uri, partName: String): MultipartBody.Part? {
        val file = uriToFile(context, uri) ?: return null
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val extension = when (context.contentResolver.getType(uri)) {
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.$extension")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(uploadResult = Resource.Idle) }
    }
}
