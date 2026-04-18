package com.duongnd.kytucxa.data.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.core.utils.handleResponseResource
import com.duongnd.kytucxa.core.utils.safeApiCall
import com.duongnd.kytucxa.data.remote.api.AuthApi
import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import com.duongnd.kytucxa.data.remote.dto.auth.UpdateProfileRequest
import com.duongnd.kytucxa.data.remote.dto.auth.fcm.FcmRequest
import com.duongnd.kytucxa.data.remote.dto.auth.fcm.FcmResponse
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.LoginResponse
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.kytucxa.data.remote.dto.auth.register.RegisterResponse
import com.duongnd.kytucxa.data.remote.dto.auth.verify.VerifyOtpRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.RefreshTokenRequest
import com.duongnd.kytucxa.data.remote.dto.auth.login.TokenDTO
import com.duongnd.kytucxa.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun register(registerRequest: RegisterRequest): Result<ApiResponse<RegisterResponse>> {
        return safeApiCall { authApi.registerApi(registerRequest) }
    }

    override suspend fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> {
        return handleResponseResource {
            authApi.loginApi(loginRequest)
        }.onEach { resource ->
            if (resource is Resource.Success) {
                sessionManager.saveAccessToken(resource.data.tokens.accessToken)
                sessionManager.saveRefreshToken(resource.data.tokens.refreshToken)
            }
        }
    }

    override suspend fun getCurrentUser(): Flow<Resource<CurrentUser>> {
        return handleResponseResource {
            authApi.getCurrentUserApi()
        }.onEach { resource ->
            if (resource is Resource.Success) {
                sessionManager.saveUser(resource.data)
            }
        }
    }

    override suspend fun logout() {
        try {
            authApi.logoutApi()
        } catch (e: Exception) {
            Timber.e(e, "Logout API error")
        } finally {
            sessionManager.clearSession()
        }
    }

    override suspend fun verifyEmail(verifyOtpRequest: VerifyOtpRequest): Flow<Resource<Unit>> {
        return handleResponseResource {
            authApi.verifyOTP(verifyOtpRequest)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(Unit, resource.message)
                is Resource.Error -> Resource.Error(resource.message, resource.data)
                Resource.Loading -> Resource.Loading
                Resource.Idle -> Resource.Idle
            }
        }
    }

    override suspend fun resendOtp(email: String): Flow<Resource<Unit>> {
        return handleResponseResource {
            authApi.resendOTP(VerifyOtpRequest(email = email, otp = ""))
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(Unit, resource.message)
                is Resource.Error -> Resource.Error(resource.message, resource.data)
                Resource.Loading -> Resource.Loading
                Resource.Idle -> Resource.Idle
            }
        }
    }

    override suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Flow<Resource<CurrentUser>> {
        return handleResponseResource {
            authApi.updateProfileApi(updateProfileRequest)
        }.onEach { resource ->
            if (resource is Resource.Success) {
                sessionManager.saveUser(resource.data)
            }
        }
    }

    override suspend fun refreshToken(): Flow<Resource<TokenDTO>> {
        return handleResponseResource {
            val refreshToken = sessionManager.getRefreshToken() ?: throw Exception("No refresh token")
            authApi.refreshTokenApi(RefreshTokenRequest(refreshToken))
        }.onEach { resource ->
            if (resource is Resource.Success) {
                sessionManager.saveAccessToken(resource.data.accessToken)
                sessionManager.saveRefreshToken(resource.data.refreshToken)
            }
        }
    }

    override suspend fun updateFcmToken(fcmToken: String): Flow<Resource<FcmResponse>> {
        return handleResponseResource {
            authApi.updateFcmTokenApi(FcmRequest(fcmToken))
        }
    }

}
