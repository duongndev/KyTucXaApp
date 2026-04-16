package com.duongnd.kytucxa.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.SessionManager
import com.duongnd.kytucxa.data.remote.dto.auth.UpdateProfileRequest
import com.duongnd.kytucxa.data.remote.dto.auth.me.CurrentUser
import com.duongnd.kytucxa.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

data class UpdateProfileState(
    val isLoading: Boolean = false,
    val currentUser: CurrentUser? = null,
    val error: String? = null,
    val isUpdateSuccess: Boolean = false,
    
    // Form fields
    val fullName: String = "",
    val email: String = "", // Email chỉ đọc
    val phoneNumber: String = "",
    val identityCard: String = "",
    val dateOfBirth: Date? = null,
    val gender: String = "",
    val university: String = "",
    val studentId: String = "",
    val major: String = "",
    val className: String = "",
    val academicYear: String = ""
)

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(UpdateProfileState())
    val state: StateFlow<UpdateProfileState> = _state.asStateFlow()

    init {
        // 1. Load ngay từ Session để hiện UI ngay lập tức
        loadUserFromSession()
        // 2. Sau đó gọi API để lấy dữ liệu mới nhất
        getCurrentUser()
    }

    private fun loadUserFromSession() {
        val userResponse = sessionManager.getUser()
        userResponse?.let {
            updateStateWithUser(it, forceOverwrite = false)
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Timber.d("UpdateProfile: Fetch success: ${resource.data}")
                        sessionManager.saveUser(resource.data)
                        updateStateWithUser(resource.data, forceOverwrite = true)
                    }
                    is Resource.Error -> {
                        Timber.e("UpdateProfile: Fetch error: ${resource.message}")
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    Resource.Loading -> {
                        if (_state.value.currentUser == null) {
                            _state.update { it.copy(isLoading = true) }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateStateWithUser(userResponse: CurrentUser, forceOverwrite: Boolean) {
        val user = userResponse.user
        val student = userResponse.student
        
        _state.update { currentState ->
            currentState.copy(
                isLoading = false,
                currentUser = userResponse,
                // Personal Info
                fullName = if (forceOverwrite) (user?.fullName ?: "") else (user?.fullName ?: currentState.fullName),
                email = user?.email ?: currentState.email, // Email luôn lấy từ server
                phoneNumber = if (forceOverwrite) (user?.phoneNumber ?: "") else (user?.phoneNumber ?: currentState.phoneNumber),
                identityCard = if (forceOverwrite) (user?.identityCard ?: "") else (user?.identityCard ?: currentState.identityCard),
                dateOfBirth = try {
                    val dobStr = user?.dateOfBirth
                    if (!dobStr.isNullOrEmpty()) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        sdf.parse(dobStr)
                    } else if (forceOverwrite) null else currentState.dateOfBirth
                } catch (e: Exception) {
                    if (forceOverwrite) null else currentState.dateOfBirth
                },
                gender = if (forceOverwrite) (user?.gender ?: "") else (user?.gender ?: currentState.gender),
                
                // Student Info - Đảm bảo lấy đúng từ object 'student'
                university = if (forceOverwrite) (student?.university ?: "") else (student?.university ?: currentState.university),
                studentId = if (forceOverwrite) (student?.studentId ?: "") else (student?.studentId ?: currentState.studentId),
                major = if (forceOverwrite) (student?.major ?: "") else (student?.major ?: currentState.major),
                className = if (forceOverwrite) (student?.className ?: "") else (student?.className ?: currentState.className),
                academicYear = if (forceOverwrite) (student?.academicYear?.toString() ?: "") else (student?.academicYear?.toString() ?: currentState.academicYear)
            )
        }
    }

    fun onFullNameChange(value: String) = _state.update { it.copy(fullName = value) }
    fun onPhoneNumberChange(value: String) = _state.update { it.copy(phoneNumber = value) }
    fun onIdentityCardChange(value: String) = _state.update { it.copy(identityCard = value) }
    fun onDateOfBirthChange(value: Date) = _state.update { it.copy(dateOfBirth = value) }
    fun onGenderChange(value: String) = _state.update { it.copy(gender = value) }
    fun onUniversityChange(value: String) = _state.update { it.copy(university = value) }
    fun onStudentIdChange(value: String) = _state.update { it.copy(studentId = value) }
    fun onMajorChange(value: String) = _state.update { it.copy(major = value) }
    fun onClassNameChange(value: String) = _state.update { it.copy(className = value) }
    fun onAcademicYearChange(value: String) = _state.update { it.copy(academicYear = value) }

    fun updateProfile() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val request = UpdateProfileRequest(
                fullName = _state.value.fullName,
                phoneNumber = _state.value.phoneNumber,
                identityCard = _state.value.identityCard,
                dateOfBirth = _state.value.dateOfBirth,
                gender = _state.value.gender,
                university = _state.value.university,
                studentId = _state.value.studentId,
                major = _state.value.major,
                className = _state.value.className,
                academicYear = _state.value.academicYear.toIntOrNull()
            )

            authRepository.updateProfile(request).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        getCurrentUser() // Refresh data
                        _state.update { it.copy(isLoading = false, isUpdateSuccess = true) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = resource.message) }
                    }
                    Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    else -> {
                        
                    }
                }
            }
        }
    }
    
    fun resetUpdateSuccess() = _state.update { it.copy(isUpdateSuccess = false) }
}
