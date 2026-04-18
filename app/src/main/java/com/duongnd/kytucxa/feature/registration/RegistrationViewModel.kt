package com.duongnd.kytucxa.feature.registration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.domain.models.FormFields
import com.duongnd.kytucxa.domain.models.TemporaryModel
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val registrationRepository: RegistrationRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _currentRegistration = MutableStateFlow<Resource<CurrentResponse>>(Resource.Idle)
    val currentRegistration: StateFlow<Resource<CurrentResponse>> = _currentRegistration.asStateFlow()

    private val _formId = MutableStateFlow<String?>(null)
    val formId: StateFlow<String?> = _formId.asStateFlow()

    // Form data state
    private val _formFields = MutableStateFlow<FormFields?>(null)
    val formFields: StateFlow<FormFields?> = _formFields.asStateFlow()

    private val _temporaryModel = MutableStateFlow<TemporaryModel?>(null)
    val temporaryModel: StateFlow<TemporaryModel?> = _temporaryModel.asStateFlow()

    private val _updateResidenceResult =
        MutableStateFlow<Resource<ResidenceResponse>>(Resource.Idle)
    val updateResidenceResult: StateFlow<Resource<ResidenceResponse>> =
        _updateResidenceResult.asStateFlow()

    init {
        loadUser()
        getCurrentRegistration()
    }

    private fun loadUser() {
        _currentUser.value = sessionManager.getUser()
    }

    fun getCurrentRegistration() {
        viewModelScope.launch {
            registrationRepository.getCurrentRegistration().collect { result ->
                _currentRegistration.value = result
                if (result is Resource.Success) {
                    val id = result.data.draft?.id ?: result.data.active?.id
                    _formId.value = id
                    Timber.d("RegistrationViewModel: Loaded formId = $id")
                }
            }
        }
    }

    fun clearRegistrationData() {
        _currentRegistration.value = Resource.Idle
        _formId.value = null
        _formFields.value = null
        _temporaryModel.value = null
    }
}
