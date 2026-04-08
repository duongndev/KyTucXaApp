package com.duongnd.kytucxa.feature.auth.verify

/**
 * Trạng thái của màn hình xác thực OTP qua Email
 */
data class VerifyEmailState(
    val email: String = "",
    val otp: String = "",
    
    // Trạng thái xử lý
    val isLoading: Boolean = false,
    val isVerifySuccess: Boolean = false,
    val isResendSuccess: Boolean = false,
    
    // Thông báo lỗi/thành công
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // Validation
    val otpError: String? = null,
    
    // Đếm ngược thời gian gửi lại (giây)
    val timerCount: Int = 120,
    val canResend: Boolean = false
)
