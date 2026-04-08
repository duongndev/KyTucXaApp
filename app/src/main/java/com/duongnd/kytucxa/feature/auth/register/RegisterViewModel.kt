package com.duongnd.kytucxa.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.ValidateUtils
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val registerState = _uiState.asStateFlow()

    /**
     * Cập nhật trạng thái đồng ý điều khoản
     */
    fun onTermsAcceptedChange(accepted: Boolean) {
        _uiState.update { it.copy(isTermsAccepted = accepted) }
    }

    /**
     * Thực hiện đăng ký tài khoản
     */
    fun registerViewModel(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        // Reset các lỗi cũ và bật loading
        _uiState.update { 
            it.copy(
                isLoading = true, 
                errorMessage = null, 
                successMessage = null,
                fullNameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null
            ) 
        }

        // Kiểm tra validation cơ bản (Client-side)
        if (!validateInput(fullName, email, password, confirmPassword)) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        // Gọi API thông qua Repository
        viewModelScope.launch {
            val request = RegisterRequest(
                fullName = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )

            authRepository.register(request)
                .onSuccess { response ->
                    // Thực thi khi API call thành công (HTTP 2xx)
                    if (response.success) {
                        // Backend xử lý thành công logic nghiệp vụ
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                isRegisterSuccess = true, 
                                successMessage = response.message,
                                email = email
                            ) 
                        }
                    } else {
                        // Backend trả về lỗi logic (ví dụ: email đã tồn tại)
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = response.message 
                            ) 
                        }
                    }
                }
                .onFailure { exception ->
                    // Thực thi khi API call thất bại (Lỗi mạng, Timeout, lỗi Server 4xx/5xx)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = exception.message ?: "Đã xảy ra lỗi không xác định" 
                        ) 
                    }
                }
        }
    }

    /**
     * Hàm kiểm tra dữ liệu đầu vào sử dụng ValidateUtils
     */
    private fun validateInput(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        var isValid = true

        if (!ValidateUtils.isNotBlank(fullName)) {
            _uiState.update { it.copy(fullNameError = "Họ tên không được để trống") }
            isValid = false
        }
        if (!ValidateUtils.isValidEmail(email)) {
            _uiState.update { 
                it.copy(
                    emailError = if (email.isBlank()) "Email không được để trống" else "Email không hợp lệ"
                ) 
            }
            isValid = false
        }
        if (!ValidateUtils.isValidPassword(password)) {
            _uiState.update { 
                it.copy(
                    passwordError = when {
                        password.isBlank() -> "Mật khẩu không được để trống"
                        password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
                        else -> "Mật khẩu phải bao gồm chữ cái, số và ký tự đặc biệt"
                    }
                ) 
            }
            isValid = false
        }
        if (!ValidateUtils.isConfirmPasswordMatch(password, confirmPassword)) {
            _uiState.update { it.copy(confirmPasswordError = "Mật khẩu xác nhận không khớp") }
            isValid = false
        }

        return isValid
    }

    /**
     * Reset trạng thái sau khi xử lý xong (ví dụ sau khi hiển thị Toast)
     */
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
