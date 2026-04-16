package com.duongnd.kytucxa.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _isLoggedIn = MutableStateFlow(getAccessToken() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val userAdapter = moshi.adapter(CurrentUser::class.java)

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_DATA = "user_data"
        private const val FCM_TOKEN = "fcm_token"
    }

    fun saveFcmToken(token: String) {
        this.sharedPreferences.edit { putString(FCM_TOKEN, token) }
    }

    fun getFcmToken(): String? {
        return this.sharedPreferences.getString(FCM_TOKEN, null)
    }

    fun saveAccessToken(token: String) {
        this.sharedPreferences.edit { putString(ACCESS_TOKEN, token) }
        _isLoggedIn.value = true
    }

    fun getAccessToken(): String? {
        return this.sharedPreferences.getString(ACCESS_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit { putString(REFRESH_TOKEN, token) }
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, null)
    }

    fun saveUser(user: CurrentUser) {
        try {
            val json = userAdapter.toJson(user)
            sharedPreferences.edit().putString(USER_DATA, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUser(): CurrentUser? {
        return try {
            val json = sharedPreferences.getString(USER_DATA, null)
            if (json != null) userAdapter.fromJson(json) else null
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        sharedPreferences.edit { clear() }
        _isLoggedIn.value = false
    }

    // Helper to check if we have a token
    fun hasToken(): Boolean = getAccessToken() != null
}
