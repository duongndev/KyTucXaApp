package com.duongnd.kytucxa.domain.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import com.duongnd.kytucxa.data.remote.dto.auth.UpdateProfileRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginResponse
import com.duongnd.kytucxa.data.remote.dto.auth.login.TokenDTO
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterResponse
import com.duongnd.kytucxa.data.remote.dto.auth.verify.VerifyOtpRequest
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository quản lý các nghiệp vụ liên quan đến xác thực (Authentication)
 */
interface AuthRepository {
    /**
     * Thực hiện đăng ký tài khoản mới
     * @param registerRequest Thông tin đăng ký từ UI
     * @return Result chứa ApiResponse và dữ liệu RegisterResponse
     */
    suspend fun register(registerRequest: RegisterRequest): Result<ApiResponse<RegisterResponse>>


    /**
     * Thực hiện đăng nhập
     * @param loginRequest Thông tin đăng nhập từ UI
     * @return Flow
     */
    suspend fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>

    /**
     * Lấy thông tin người dùng hiện tại
     * @return Flow chứa thông tin người dùng
     */
    suspend fun getCurrentUser(): Flow<Resource<CurrentUser>>

    /**
     * Đăng xuất xóa token
     */
    suspend fun logout()

    /**
     * Xác thực OTP qua email
     *
     */

    suspend fun verifyEmail(verifyOtpRequest: VerifyOtpRequest): Flow<Resource<Unit>>

    /**
     * Gửi lại mã OTP
     */
    suspend fun resendOtp(email: String): Flow<Resource<Unit>>

    /**
     * Cập nhật thông tin cá nhân
     */
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Flow<Resource<CurrentUser>>

    /**
     * Làm mới access token
     */
    suspend fun refreshToken(): Flow<Resource<TokenDTO>>
}
