package com.duongnd.kytucxa.feature.registration.temporary

import android.graphics.Bitmap
import com.duongnd.kytucxa.domain.models.TemporaryModel

/**
 * Trạng thái của màn hình nhập dữ liệu đơn đăng ký tạm trú
 */
data class TemporaryFormState(

    val temporary: TemporaryModel = TemporaryModel(
        cccd = "",
        dateOfBirth = "",
        email = "",
        fullName = "",
        gender = "",
        ownerCccd = "",
        ownerName = "",
        ownerRelation = "",
        phoneNumber = "",
        receiver = "",
        requestContent = ""
    ),
    val signatureBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Kiểm tra tính hợp lệ của form.
     * Các trường trong TemporaryModel là bắt buộc.
     */

    val isFormValid: Boolean
        get() = with(temporary) {
            cccd.isNotBlank() &&
            dateOfBirth.isNotBlank() &&
            email.isNotBlank() &&
            fullName.isNotBlank() &&
            gender.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            receiver.isNotBlank() &&
            requestContent.isNotBlank()
        }
}
