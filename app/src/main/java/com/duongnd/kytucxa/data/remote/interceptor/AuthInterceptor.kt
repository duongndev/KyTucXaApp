package com.duongnd.kytucxa.data.remote.interceptor

import com.duongnd.kytucxa.core.utils.SessionEvent
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.api.AuthApi
import com.duongnd.kytucxa.data.remote.dto.auth.login.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    private val sessionEvent: SessionEvent,
    private val authApiProvider: Provider<AuthApi> // Sử dụng Provider để tránh Dependency Cycle
) : Interceptor {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token for login/register/refresh endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("auth/login") || path.contains("auth/register") || path.contains("auth/refresh-token")) {
            return chain.proceed(originalRequest)
        }

        // Thêm token vào request ban đầu
        val requestWithToken = addTokenToRequest(originalRequest)
        var response = chain.proceed(requestWithToken)

        // Nếu nhận mã 401 (Unauthorized) - Access Token hết hạn
        if (response.code == 401) {
            Timber.e("Access Token hết hạn (401). Đang thử refresh...")

            synchronized(this) {
                // Kiểm tra lại token hiện tại, nếu nó khác với token lúc gửi request 
                // nghĩa là đã có một request khác refresh thành công rồi
                val currentToken = sessionManager.getAccessToken()
                val requestToken = requestWithToken.header("Authorization")?.removePrefix("Bearer ")

                val finalToken = if (currentToken != requestToken && currentToken != null) {
                    currentToken
                } else {
                    // Nếu chưa refresh, tiến hành gọi API refresh token
                    runBlocking {
                        handleTokenRefresh()
                    }
                }

                if (finalToken != null) {
                    // Refresh thành công hoặc đã có token mới, thực hiện lại request
                    response.close() // Quan trọng: Đóng response cũ trước khi retry
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $finalToken")
                        .build()
                    response = chain.proceed(newRequest)
                } else {
                    // Refresh thất bại (Refresh Token hết hạn) -> Logout và hiện popup
                    Timber.e("Refresh Token cũng hết hạn. Yêu cầu đăng nhập lại.")
                    scope.launch {
                        sessionManager.clearSession()
                        sessionEvent.emitSessionExpired()
                    }
                }
            }
        }

        return response
    }

    private fun addTokenToRequest(request: Request): Request {
        val token = sessionManager.getAccessToken()
        return if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
    }

    private suspend fun handleTokenRefresh(): String? {
        val refreshToken = sessionManager.getRefreshToken() ?: return null

        return try {
            val response = authApiProvider.get().refreshTokenApi(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                    val data = apiResponse.data
                    sessionManager.saveAccessToken(data.accessToken)
                    sessionManager.saveRefreshToken(data.refreshToken)
                    data.accessToken
                } else {
                    null
                }
            } else {
                Timber.e("Refresh token API failed with code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Lỗi khi refresh token")
            null
        }
    }
}
