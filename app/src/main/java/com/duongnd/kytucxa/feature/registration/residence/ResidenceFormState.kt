package com.duongnd.kytucxa.feature.registration.residence

import android.graphics.Bitmap
import com.duongnd.kytucxa.domain.models.ResidenceModel

/**
 * Trạng thái của màn hình nhập dữ liệu đơn đăng ký thuê nhà ở
 */
data class ResidenceFormState(
    val residence: ResidenceModel = ResidenceModel(
        academicYear = "",
        cccd = "",
        cccdIdIssueDate = "",
        cccdIdIssuePlace = "",
        className = "",
        dateOfBirth = "",
        department = "",
        dormName = "",
        duration = "",
        email = "",
        emergencyContact = "",
        fullName = "",
        gender = "",
        major = "",
        permanentAddress = "",
        phoneNumber = "",
        schoolName = "",
        studentId = ""
    ),
    val priorityType: String = "",
    val signatureBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Kiểm tra tính hợp lệ của form.
     * Các trường trong ResidenceModel là bắt buộc.
     */
    val isFormValid: Boolean
        get() = with(residence) {
            fullName.isNotBlank() &&
            gender.isNotBlank() &&
            dateOfBirth.isNotBlank() &&
            cccd.length == 12 &&
            cccdIdIssueDate.isNotBlank() &&
            cccdIdIssuePlace.isNotBlank() &&
            permanentAddress.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            email.isNotBlank() &&
            emergencyContact.isNotBlank() &&
            schoolName.isNotBlank() &&
            academicYear.isNotBlank() &&
            className.isNotBlank() &&
            department.isNotBlank() &&
            major.isNotBlank() &&
            studentId.isNotBlank() &&
            dormName.isNotBlank() &&
            duration.isNotBlank()
        }
}
