package com.duongnd.kytucxa.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            val token = sessionManager.getAccessToken()
            Timber.d("Splash: Checking session, token exists: ${!token.isNullOrEmpty()}")

            if (token.isNullOrEmpty()) {
                _destination.value = SplashDestination.Login
                return@launch
            }

            authRepository.getCurrentUser().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val userResponse = resource.data
                        val user = userResponse.user
                        val student = userResponse.student
                        
                        if (user == null) {
                            _destination.value = SplashDestination.UpdateProfile
                            return@collect
                        }

                        // 1. Kiểm tra thông tin cá nhân đầy đủ
                        val isPersonalInfoComplete = !user.fullName.isNullOrBlank() &&
                                !user.identityCard.isNullOrBlank() &&
                                !user.phoneNumber.isNullOrBlank() &&
                                !user.gender.isNullOrBlank() &&
                                !user.dateOfBirth.isNullOrBlank()

                        // 2. Kiểm tra thông tin sinh viên đầy đủ
                        val isStudentInfoComplete = student != null &&
                                !student.university.isNullOrBlank() &&
                                !student.studentId.isNullOrBlank() &&
                                !student.major.isNullOrBlank() &&
                                !student.className.isNullOrBlank() &&
                                !student.academicYear.isNullOrBlank()
                        
                        // 3. Kiểm tra trạng thái xác thực tài khoản
                        val isAccountVerified = user.isAccountVerified
                        
                        Timber.d("Splash: Profile complete: $isPersonalInfoComplete, Student complete: $isStudentInfoComplete, Verified: $isAccountVerified")
                        
                        if (!isPersonalInfoComplete || !isStudentInfoComplete) {
                            // Nếu thiếu thông tin cá nhân hoặc sinh viên -> Yêu cầu cập nhật
                            _destination.value = SplashDestination.UpdateProfile
                        } else if (!isAccountVerified) {
                            // Nếu thông tin đã đầy đủ nhưng tài khoản chưa được BQL xác thực -> Chuyển đến luồng chọn phương thức nộp hồ sơ
                            _destination.value = SplashDestination.Registration
                        } else {
                            // Đã đầy đủ thông tin và đã xác thực -> Vào Home
                            _destination.value = SplashDestination.Home
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("Splash: Get user error: ${resource.message}")
                        _destination.value = SplashDestination.Login
                    }
                    is Resource.Loading -> {
                        _destination.value = SplashDestination.Loading
                    }
                    else -> {}
                }
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
