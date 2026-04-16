package com.duongnd.kytucxa.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionEvent
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val sessionEvent: SessionEvent,
    val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun updateFcmToken(token: String) {
        // Chỉ gọi API nếu đã đăng nhập
        if (!sessionManager.hasToken()) {
            Timber.d("User not logged in, skipping FCM token update")
            return
        }

        // Kiểm tra nếu token mới giống với token đã lưu thì không gọi API
        val savedToken = sessionManager.getFcmToken()
        if (token == savedToken) {
            Timber.d("FCM token matches saved token, skipping update")
            return
        }

        viewModelScope.launch {
            authRepository.updateFcmToken(token).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Timber.d("Update FCM token to server success")
                        // Lưu token mới vào session sau khi cập nhật thành công
                        sessionManager.saveFcmToken(token)
                    }
                    is Resource.Error -> {
                        Timber.e("Update FCM token to server failed: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        Timber.d("Updating FCM token...")
                    }
                    else -> {
                        // Handle other cases if necessary
                    }
                }
            }
        }
    }
}
