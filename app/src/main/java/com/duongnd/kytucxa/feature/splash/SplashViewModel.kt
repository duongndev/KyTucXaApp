package com.duongnd.kytucxa.feature.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.registration.RegistrationStatus
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
            // 1. Kiểm tra kết nối Internet
            _destination.value = SplashDestination.CheckingNetwork
            if (!com.duongnd.kytucxa.core.utils.isNetworkAvailable(context)) {
                _destination.value = SplashDestination.NoInternet
                return@launch
            }

            // 2. Kiểm tra quyền (Permissions)
            _destination.value = SplashDestination.CheckingPermissions
            if (!hasRequiredPermissions()) {
                _destination.value = SplashDestination.RequestPermissions
                return@launch
            }

            // 3. Nếu mọi thứ ổn, bắt đầu vào Splash chính (Auth)
            startAuthCheck()
        }
    }

    private suspend fun startAuthCheck() {
        _destination.value = SplashDestination.Loading

        // Kiểm tra access token
        val token = sessionManager.getAccessToken()
        Timber.d("Splash: Checking session, token exists: ${!token.isNullOrEmpty()}")

        if (token.isNullOrEmpty()) {
            _destination.value = SplashDestination.Login
            return
        }

        // Kiểm tra thông tin người dùng từ auth/me
        val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }

        when (userResource) {
            is Resource.Success -> {
                handleUserLogic(userResource.data)
            }

            is Resource.Error -> {
                // Thử refresh token
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

        // 3. Kiểm tra thông tin cá nhân
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

        Timber.d("Splash: Profile complete: $isPersonalInfoComplete, Student complete: $isStudentInfoComplete, Verified: ${user.isAccountVerified}")

        if (!isPersonalInfoComplete || !isStudentInfoComplete) {
            // Thiếu -> màn hình cập nhật thông tin
            _destination.value = SplashDestination.UpdateProfile
        } else {
            // Đủ -> Kiểm tra Draft Registration Form
            checkRegistrationDraft()
        }
    }

    private suspend fun checkRegistrationDraft() {
        val draftResource =
            registrationRepository.getCurrentDraft().first { it !is Resource.Loading }

        if (draftResource is Resource.Success) {
            val response = draftResource.data
            val registration = response.registrationForm

            // 1. Kiểm tra đã bắt đầu chưa (hasDraft)
            if (!response.hasDraft || registration == null) {
                _destination.value = SplashDestination.Registration(null)
                return
            }

            // 2. Nếu là bản nháp (DRAFT), thông báo để hiển thị Dialog
            if (registration.status.equals(RegistrationStatus.DRAFT.name, ignoreCase = true)) {
                _destination.value = SplashDestination.Registration(response)
            } else {
                // Các trạng thái khác (PENDING, APPROVED, v.v.)
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
        } else {
            _destination.value = SplashDestination.Registration(null)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val permissions = mutableListOf<String>()

        // Camera & Storage (Android 13+ handles storage differently)
        permissions.add(android.Manifest.permission.CAMERA)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Notifications (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
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
}
