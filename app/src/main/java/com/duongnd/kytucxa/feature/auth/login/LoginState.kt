package com.duongnd.kytucxa.feature.auth.login

import com.duongnd.kytucxa.data.remote.dto.StudentDTO
import com.duongnd.kytucxa.data.remote.dto.UserDTO

/**
 * Trạng thái của màn hình Đăng nhập
 */
data class LoginState(
    // Trạng thái chung
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    val user: UserDTO? = null,
    val student: StudentDTO? = null,

    // Trạng thái validation (Lỗi hiển thị trên từng TextField)
    val emailError: String? = null,
    val passwordError: String? = null,
    val isEmailVerified: Boolean = false

)
