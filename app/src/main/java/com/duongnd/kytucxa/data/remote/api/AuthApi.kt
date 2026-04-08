package com.duongnd.kytucxa.data.remote.api

import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import com.duongnd.kytucxa.data.remote.dto.auth.UpdateProfileRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginResponse
import com.duongnd.kytucxa.data.remote.dto.auth.login.RefreshTokenRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.TokenDTO
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterResponse
import com.duongnd.kytucxa.data.remote.dto.auth.verify.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/auth/login")
    suspend fun loginApi(@Body loginRequest: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("/api/auth/register")
    suspend fun registerApi(@Body registerRequest: RegisterRequest): ApiResponse<RegisterResponse>

    @POST("/api/auth/refresh-token")
    suspend fun refreshTokenApi(
        @Body refreshToken: RefreshTokenRequest
    ): Response<ApiResponse<TokenDTO>>

    @GET("/api/auth/me")
    suspend fun getCurrentUserApi(): Response<ApiResponse<CurrentUser>>

    @PATCH("/api/auth/update-profile")
    suspend fun updateProfileApi(@Body updateProfileRequest: UpdateProfileRequest): Response<ApiResponse<CurrentUser>>

    @POST("/api/auth/logout")
    suspend fun logoutApi(): Response<ApiResponse<Map<String, Any?>?>>


    @POST("/api/auth/verify-otp")
    suspend fun verifyOTP(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Response<ApiResponse<Map<String, Any?>?>>


    @POST("/api/auth/resend-otp")
    suspend fun resendOTP(
        @Body verifyOtpRequest: VerifyOtpRequest
    ): Response<ApiResponse<Map<String, Any?>?>>
}
