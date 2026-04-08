package com.duongnd.kytucxa.feature.auth.register

/**
 * Trạng thái của màn hình Đăng ký
 */
data class RegisterState(
    // Trạng thái chung
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val email: String = "",

    // Trạng thái validation (Lỗi hiển thị trên từng TextField)
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val fullNameError: String? = null,

    // Trạng thái các điều kiện phụ
    val isTermsAccepted: Boolean = false
)
