package com.duongnd.kytucxa.feature.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentType
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftRegistrationDTO
import com.duongnd.kytucxa.domain.repository.AuthRepository
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
                val refreshResource =
                    authRepository.refreshToken().first { it !is Resource.Loading }
                if (refreshResource is Resource.Success) {
                    val retryResource =
                        authRepository.getCurrentUser().first { it !is Resource.Loading }
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
            checkCurrentRegistration()
        }
    }

    private suspend fun checkCurrentRegistration() {
        val resource =
            registrationRepository.getCurrentRegistration().first { it !is Resource.Loading }

        Timber.d("Splash: checkCurrentRegistration result: $resource")

        when (resource) {
            is Resource.Success -> {
                val response = resource.data
                if (response == null || !response.hasRegistration) {
                    // Trường hợp 3: Chưa có đơn -> Cho phép tạo đơn mới
                    _destination.value = SplashDestination.Registration(null)
                    return
                }

                when (response.type) {
                    CurrentType.DRAFT.name.lowercase() -> {
                        // Cache the response before navigating
                        registrationRepository.setCachedRegistration(response)
                        // Trường hợp 1: Đang điền dở -> Chuyển sang Registration để SplashScreen hiển thị dialog xác nhận
                        _destination.value = SplashDestination.Registration(response)
                    }

                    CurrentType.ACTIVE.name.lowercase() -> {
                        registrationRepository.setCachedRegistration(response)
                        // Trường hợp 2: Đã submit, đang xử lý -> Vào màn hình theo dõi đơn
                        _destination.value = SplashDestination.Tracking(response)
                    }

                    CurrentType.APPROVED.name.lowercase() -> {
                        registrationRepository.setCachedRegistration(response)
                        // Trường hợp 3: Đã duyệt -> Vào màn hình chính
                        _destination.value = SplashDestination.Home
                    }

                    else -> {
                        _destination.value = SplashDestination.Registration(null)
                    }
                }
            }

            is Resource.Error -> {
                // Nếu lỗi API, mặc định cho về màn hình Registration để thử lại hoặc tạo mới
                _destination.value = SplashDestination.Registration(null)
            }

            else -> _destination.value = SplashDestination.Registration(null)
        }
    }

    private fun handleDraftProgress(progress: Int, draft: DraftRegistrationDTO?) {
        when {
            progress < 25 -> _destination.value = SplashDestination.Step1Residence(draft)
            progress < 50 -> _destination.value = SplashDestination.Step2Temporary(draft)
            progress < 75 -> _destination.value = SplashDestination.Step3Documents(draft)
            else -> _destination.value = SplashDestination.SubmitReady(draft)
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
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                it
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
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
                    registrationRepository.clearCachedRegistration()
                    _destination.value = SplashDestination.Registration(null)
                }
            }
        }
    }
}
