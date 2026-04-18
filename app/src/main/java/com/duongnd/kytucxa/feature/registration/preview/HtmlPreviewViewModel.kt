package com.duongnd.kytucxa.feature.registration.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HtmlPreviewViewModel @Inject constructor(
    private val previewRepository: PreviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HtmlPreviewUiState())
    val uiState: StateFlow<HtmlPreviewUiState> = _uiState.asStateFlow()

    fun loadPreview(formId: String, type: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true,
                htmlContent = null,
                errorMessage = null
            ) }
            
            val flow = if (type == "residence") {
                previewRepository.getPreviewResidence(formId)
            } else {
                previewRepository.getPreviewTemporary(formId)
            }

            flow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            htmlContent = resource.data,
                            errorMessage = null
                        ) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            htmlContent = null,
                            errorMessage = resource.message
                        ) }
                    }
                    else -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { HtmlPreviewUiState() }
    }
}
