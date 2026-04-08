package com.duongnd.kytucxa.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.ValidateUtils
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginRequest
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val loginState = _uiState.asStateFlow()


    fun loginViewModel(email: String, password: String) {

        // reset
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
                emailError = null,
                passwordError = null,
                isLoginSuccess = false,
                user = null,
                student = null,
                isEmailVerified = true
            )
        }

        if (!validateInput(email, password)) return

        viewModelScope.launch {

            authRepository.login(LoginRequest(email, password))
                .collectLatest { rs ->

                    _uiState.update { current ->

                        when (rs) {

                            is Resource.Loading -> {
                                current.copy(isLoading = true)
                            }

                            is Resource.Success -> {
                                current.copy(
                                    isLoading = false,
                                    isLoginSuccess = true,
                                    successMessage = "Đăng nhập thành công",
                                    errorMessage = null,
                                    user = rs.data.user,
                                    student = rs.data.student
                                )
                            }

                            is Resource.Error -> {

                                val data = rs.data as? JSONObject
                                Timber.d(data.toString())
                                val iVerified = data?.optBoolean("isEmailVerified") ?: true
                                Timber.d(iVerified.toString())

                                current.copy(
                                    isLoading = false,
                                    errorMessage = rs.message,
                                    successMessage = null,
                                    isEmailVerified = iVerified
                                )
                            }

                            else -> {
                                current.copy(
                                    isLoading = false,
                                    errorMessage = "Đã xảy ra lỗi không xác định",
                                    successMessage = null
                                )
                            }
                        }
                    }
                }
        }
    }


    /**
     * Hàm kiểm tra dữ liệu đầu vào sử dụng ValidateUtils
     */
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        // Kiểm tra email sử dụng ValidateUtils
        if (!ValidateUtils.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(
                emailError = if (email.isBlank()) "Email không được để trống" else "Email không hợp lệ"
            )
            isValid = false
        } else {
            _uiState.value = _uiState.value.copy(emailError = null)
        }

        // Kiểm tra mật khẩu sử dụng ValidateUtils
        if (!ValidateUtils.isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(
                passwordError = when {
                    password.isBlank() -> "Mật khẩu không được để trống"
                    password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
                    else -> "Mật khẩu phải chứa ít nhất 1 chữ cái hoa, 1 chữ cái thường, 1 số và 1 ký tự đặc biệt"
                }
            )
            isValid = false
        } else {
            _uiState.value = _uiState.value.copy(passwordError = null)
        }

        return isValid
    }


    /**
     * Reset trạng thái sau khi xử lý xong (ví dụ sau khi hiển thị Toast)
     */
    fun clearMessages() {
        _uiState.update { 
            it.copy(
                errorMessage = null, 
                successMessage = null,
                isEmailVerified = true 
            ) 
        }
    }
}
