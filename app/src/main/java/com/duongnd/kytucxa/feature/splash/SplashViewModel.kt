package com.duongnd.kytucxa.feature.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.RegistrationStatus
import com.duongnd.kytucxa.domain.repository.AuthRepository
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val registrationRepository: RegistrationRepository,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Idle)
    val destination = _destination.asStateFlow()

    fun checkInitialRequirements() {
        viewModelScope.launch {
            _destination.value = SplashDestination.CheckingNetwork
            if (!com.duongnd.kytucxa.core.utils.isNetworkAvailable(context)) {
                _destination.value = SplashDestination.NoInternet
                return@launch
            }

            _destination.value = SplashDestination.CheckingPermissions
            if (!hasRequiredPermissions()) {
                _destination.value = SplashDestination.RequestPermissions
                return@launch
            }

            startAuthCheck()
        }
    }

    private suspend fun startAuthCheck() {
        _destination.value = SplashDestination.Loading
        val token = sessionManager.getAccessToken()
        
        if (token.isNullOrEmpty()) {
            _destination.value = SplashDestination.Login
            return
        }

        val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }

        when (userResource) {
            is Resource.Success -> {
                handleUserLogic(userResource.data)
            }
            is Resource.Error -> {
                val refreshResource = authRepository.refreshToken().first { it !is Resource.Loading }
                if (refreshResource is Resource.Success) {
                    val retryResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                    if (retryResource is Resource.Success) {
                        handleUserLogic(retryResource.data)
                    } else {
                        _destination.value = SplashDestination.Login
                    }
                } else {
                    _destination.value = SplashDestination.Login
                }
            }
            else -> _destination.value = SplashDestination.Login
        }
    }

    private suspend fun handleUserLogic(userResponse: CurrentUser) {
        val user = userResponse.user
        val student = userResponse.student

        if (user == null) {
            _destination.value = SplashDestination.UpdateProfile
            return
        }

        val isPersonalInfoComplete = !user.fullName.isNullOrBlank() &&
                !user.identityCard.isNullOrBlank() &&
                !user.phoneNumber.isNullOrBlank() &&
                !user.gender.isNullOrBlank() &&
                !user.dateOfBirth.isNullOrBlank()

        val isStudentInfoComplete = student != null &&
                !student.university.isNullOrBlank() &&
                !student.studentId.isNullOrBlank() &&
                !student.major.isNullOrBlank() &&
                !student.className.isNullOrBlank() &&
                !student.academicYear.isNullOrBlank()

        if (!isPersonalInfoComplete || !isStudentInfoComplete) {
            _destination.value = SplashDestination.UpdateProfile
        } else {
            checkRegistrationDraft()
        }
    }

    private suspend fun checkRegistrationDraft() {
        val draftResource = registrationRepository.getCurrentDraft().first { it !is Resource.Loading }
        
        Timber.d("Splash: checkRegistrationDraft result: $draftResource")

        when (draftResource) {
            is Resource.Success -> {
                val response = draftResource.data
                Timber.d("Splash: Success Data: $response")
                if (isDraftExists(response)) {
                    if (response.registrationForm != null) {
                        handleRegistrationStatus(response)
                    } else {
                        _destination.value = SplashDestination.Registration(response)
                    }
                } else {
                    _destination.value = SplashDestination.Registration(null)
                }
            }

            is Resource.Error -> {
                val rawData = draftResource.data
                Timber.d("Splash: Error Data Type: ${rawData?.javaClass?.name}, Data: $rawData")

                val draftData = when (rawData) {
                    is DraftResponse -> rawData
                    is JSONObject -> {
                        DraftResponse(
                            hasDraft = true,
                            existingFormId = rawData.optString("existingFormId").takeIf { it.isNotEmpty() },
                            existingFormCode = rawData.optString("existingFormCode").takeIf { it.isNotEmpty() },
                            existingStatus = rawData.optString("existingStatus").takeIf { it.isNotEmpty() },
                            progressPercent = if (rawData.has("progressPercent")) rawData.optInt("progressPercent") else null
                        )
                    }
                    is Map<*, *> -> {
                        DraftResponse(
                            hasDraft = true,
                            existingFormId = rawData["existingFormId"] as? String,
                            existingFormCode = rawData["existingFormCode"] as? String,
                            existingStatus = rawData["existingStatus"] as? String,
                            progressPercent = (rawData["progressPercent"] as? Number)?.toInt()
                        )
                    }
                    else -> null
                }

                if (draftData != null && isDraftExists(draftData)) {
                    Timber.d("Splash: Found draft in error data: $draftData")
                    _destination.value = SplashDestination.Registration(draftData)
                } else {
                    _destination.value = SplashDestination.Registration(null)
                }
            }

            else -> _destination.value = SplashDestination.Registration(null)
        }
    }

    private fun isDraftExists(draft: DraftResponse): Boolean {
        // Kiểm tra tất cả các dấu hiệu của một đơn đang tồn tại
        // Bao gồm cả trường hợp progressPercent = 0
        val exists = draft.registrationForm != null || 
               draft.hasDraft || 
               draft.existingFormId != null || 
               draft.progressPercent != null
        Timber.d("Splash: isDraftExists checking: registrationForm=${draft.registrationForm!=null}, hasDraft=${draft.hasDraft}, existingFormId=${draft.existingFormId}, progressPercent=${draft.progressPercent} -> Result: $exists")
        return exists
    }

    private fun handleRegistrationStatus(response: DraftResponse) {
        val registration = response.registrationForm ?: return
        Timber.d("Splash: Handling status for ${registration.status}")
        
        if (registration.status.equals(RegistrationStatus.DRAFT.name, ignoreCase = true)) {
            _destination.value = SplashDestination.Registration(response)
        } else {
            when {
                registration.status.equals(RegistrationStatus.PENDING.name, ignoreCase = true) -> {
                    _destination.value = SplashDestination.Pending(registration.id)
                }
                registration.status.equals(RegistrationStatus.REQUIRES_SUPPLEMENT.name, ignoreCase = true) -> {
                    _destination.value = SplashDestination.RequiresSupplement(registration.id)
                }
                registration.status.equals(RegistrationStatus.APPROVED.name, ignoreCase = true) -> {
                    _destination.value = SplashDestination.Home
                }
                registration.status.equals(RegistrationStatus.REJECTED.name, ignoreCase = true) -> {
                    _destination.value = SplashDestination.Registration(null)
                }
                else -> {
                    _destination.value = SplashDestination.Home
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = mutableListOf<String>()
        permissions.add(android.Manifest.permission.CAMERA)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return permissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _destination.value = SplashDestination.Login
        }
    }

    fun deleteDraft(id: String) {
        viewModelScope.launch {
            registrationRepository.deleteRegistrationForm(id).collect { resource ->
                if (resource is Resource.Success) {
                    _destination.value = SplashDestination.Registration(null)
                }
            }
        }
    }
}
