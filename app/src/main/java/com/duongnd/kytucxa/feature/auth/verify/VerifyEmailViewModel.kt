package com.duongnd.kytucxa.feature.auth.verify

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.auth.verify.VerifyOtpRequest
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Lấy email từ navigation arguments
        val email = savedStateHandle.get<String>("email") ?: ""
        _uiState.update { it.copy(email = email) }
        startTimer()
    }

    /**
     * Cập nhật mã OTP khi người dùng nhập
     */
    fun onOtpChange(otp: String) {
        if (otp.length <= 6) {
            _uiState.update { it.copy(otp = otp, otpError = null) }
        }
    }

    /**
     * Gửi yêu cầu xác thực OTP
     */
    fun verifyOtp() {
        val email = _uiState.value.email
        val otp = _uiState.value.otp

        if (otp.length < 6) {
            _uiState.update { it.copy(otpError = "Vui lòng nhập đủ 6 chữ số") }
            return
        }

        viewModelScope.launch {
            authRepository.verifyEmail(VerifyOtpRequest(email, otp)).collectLatest { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> {
                            current.copy(isLoading = true, errorMessage = null)
                        }
                        is Resource.Success -> {
                            current.copy(
                                isLoading = false, 
                                isVerifySuccess = true,
                                successMessage = resource.message
                            ) 
                        }
                        is Resource.Error -> {
                            current.copy(isLoading = false, errorMessage = resource.message)
                        }
                        else -> current
                    }
                }
            }
        }
    }

    /**
     * Gửi lại mã OTP mới
     */
    fun resendOtp() {
        val email = _uiState.value.email
        if (email.isEmpty()) return

        viewModelScope.launch {
            authRepository.resendOtp(email).collectLatest { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> {
                            current.copy(isLoading = true, errorMessage = null, isResendSuccess = false)
                        }
                        is Resource.Success -> {
                            startTimer() // Reset timer sau khi gửi lại thành công
                            current.copy(
                                isLoading = false, 
                                isResendSuccess = true,
                                successMessage = "Mã xác thực mới đã được gửi đến email của bạn"
                            ) 
                        }
                        is Resource.Error -> {
                            current.copy(isLoading = false, errorMessage = resource.message)
                        }
                        else -> current
                    }
                }
            }
        }
    }

    /**
     * Quản lý bộ đếm ngược thời gian gửi lại mã
     */
    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerCount = 120, canResend = false) }
        
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerCount > 0) {
                delay(1000L)
                _uiState.update { it.copy(timerCount = it.timerCount - 1) }
            }
            _uiState.update { it.copy(canResend = true) }
        }
    }

    /**
     * Xóa các thông báo lỗi/thành công sau khi hiển thị
     */
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, isResendSuccess = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
