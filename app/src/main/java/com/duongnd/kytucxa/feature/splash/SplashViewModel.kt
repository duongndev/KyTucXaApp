package com.duongnd.kytucxa.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class SplashDestination {
    data object Loading : SplashDestination()
    data object Login : SplashDestination()
    data object Home : SplashDestination()
    data object UpdateProfile : SplashDestination()
    data object Registration : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination = _destination.asStateFlow()

    fun checkUserSession() {
        viewModelScope.launch {
            _destination.value = SplashDestination.Loading
            
            // 1. Kiểm tra access token
            val token = sessionManager.getAccessToken()
            Timber.d("Splash: Checking session, token exists: ${!token.isNullOrEmpty()}")

            if (token.isNullOrEmpty()) {
                _destination.value = SplashDestination.Login
                return@launch
            }

            // 2. Kiểm tra thông tin người dùng từ auth/me
            val userResource = authRepository.getCurrentUser().first { it !is Resource.Loading }

            when (userResource) {
                is Resource.Success -> {
                    handleUserLogic(userResource.data)
                }
                is Resource.Error -> {
                    // Lỗi hoặc token hết hạn -> gọi api auth/refresh-token
                    Timber.d("Splash: Get user error, attempting refresh token: ${userResource.message}")
                    val refreshResource = authRepository.refreshToken().first { it !is Resource.Loading }
                    
                    if (refreshResource is Resource.Success) {
                        // Refresh thành công -> Thử lấy lại thông tin user
                        val retryResource = authRepository.getCurrentUser().first { it !is Resource.Loading }
                        if (retryResource is Resource.Success) {
                            handleUserLogic(retryResource.data)
                        } else {
                            _destination.value = SplashDestination.Login
                        }
                    } else {
                        // Lỗi khi gọi api refresh token -> màn hình đăng nhập
                        _destination.value = SplashDestination.Login
                    }
                }
                else -> {
                    _destination.value = SplashDestination.Login
                }
            }
        }
    }

    private fun handleUserLogic(userResponse: CurrentUser) {
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
            // Đủ -> chuyển sang bước 4: Kiểm tra trạng thái tài khoản isAccountVerified
            if (user.isAccountVerified) {
                // True -> màn hình home
                _destination.value = SplashDestination.Home
            } else {
                // False -> màn hình chọn phương thức nộp hồ sơ
                _destination.value = SplashDestination.Registration
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _destination.value = SplashDestination.Login
        }
    }
}
