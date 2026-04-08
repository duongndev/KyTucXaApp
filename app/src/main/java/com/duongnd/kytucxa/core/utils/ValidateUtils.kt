package com.duongnd.kytucxa.core.utils

import android.util.Patterns
import java.util.Calendar

/**
 * Lớp tiện ích để kiểm tra tính hợp lệ của dữ liệu (Validation)
 */
object ValidateUtils {

    /**
     * Kết quả trả về của hàm validate
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )

    /**
     * Kiểm tra định dạng Email
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Kiểm tra độ dài mật khẩu
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return hasLetter && hasDigit && hasSpecialChar
    }

    /**
     * Kiểm tra số điện thoại (Tối thiểu 10 số)
     */
    fun isValidPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length >= 10
    }

    /**
     * Kiểm tra chuỗi không được để trống
     */
    fun isNotBlank(value: String): Boolean {
        return value.isNotBlank()
    }

    /**
     * Kiểm tra mật khẩu xác nhận có khớp không
     */
    fun isConfirmPasswordMatch(password: String, confirm: String): Boolean {
        return password == confirm
    }

    /**
     * Validate CCCD/CMND dựa trên logic JS cung cấp
     */
    fun validateCCCD(cccd: String?): ValidationResult {
        // 1. Kiểm tra đầu vào
        if (cccd.isNullOrBlank()) {
            return ValidationResult(false, "Số CCCD không được để trống")
        }

        // 2. Xóa khoảng trắng và ký tự đặc biệt
        val cleanedCCCD = cccd.replace("\\s+".toRegex(), "").replace("[-_]".toRegex(), "")

        // 3. Kiểm tra độ dài (9 cho CMND, 12 cho CCCD)
        if (cleanedCCCD.length != 9 && cleanedCCCD.length != 12) {
            return ValidationResult(false, "Số CCCD phải gồm 9 số (CMND) hoặc 12 số (CCCD)")
        }

        // 4. Kiểm tra chỉ chứa số
        if (!cleanedCCCD.all { it.isDigit() }) {
            return ValidationResult(false, "Số CCCD chỉ được chứa chữ số")
        }

        // 5. Validate chi tiết theo độ dài
        if (cleanedCCCD.length == 9) {
            // Validate CMND (9 số)
            val validCMNDPrefixes = listOf('0', '1', '2', '3')
            if (cleanedCCCD[0] !in validCMNDPrefixes) {
                return ValidationResult(false, "Số CMND không hợp lệ")
            }
        } else {
            // Validate CCCD (12 số)
            // Kiểm tra mã tỉnh thành (3 số đầu)
            val provinceCode = cleanedCCCD.substring(0, 3)
            val validProvinceCodes = listOf(
                "001", "002", "004", "006", "008", "010", "011", "012", "014", "015", "016", "017", "018", "019", "020",
                "021", "022", "024", "025", "026", "027", "028", "029", "030", "031", "032", "033", "034", "035", "036",
                "037", "038", "040", "042", "044", "045", "046", "048", "049", "050", "052", "054", "056", "058", "060",
                "062", "064", "066", "067", "068", "070", "072", "074", "076", "078", "080", "082", "084", "086", "088", "089",
                "091", "092", "093", "094", "095", "096", "097", "098", "099"
            )

            if (provinceCode !in validProvinceCodes) {
                return ValidationResult(false, "Mã tỉnh thành trong số CCCD không hợp lệ")
            }

            // Kiểm tra giới tính (số thứ 4, index 3)
            val genderDigit = cleanedCCCD[3]
            if (genderDigit !in listOf('0', '1', '2', '3')) {
                return ValidationResult(false, "Số CCCD không hợp lệ (mã giới tính)")
            }

            // Kiểm tra năm sinh (số 5-6, index 4-5)
            val birthYearSuffix = cleanedCCCD.substring(4, 6).toIntOrNull() ?: return ValidationResult(false, "Năm sinh không hợp lệ")
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentCentury = (currentYear / 100) * 100

            // Xác định thế kỷ dựa trên số giới tính
            // 0, 1: Thế kỷ 20 (1900-1999) | 2, 3: Thế kỷ 21 (2000-2099) | 4, 5: Thế kỷ 22...
            val century = when (genderDigit) {
                '0', '1' -> 1900
                '2', '3' -> 2000
                '4', '5' -> 2100
                '6', '7' -> 2200
                '8', '9' -> 2300
                else -> 1900
            }

            val fullYear = century + birthYearSuffix
            if (fullYear !in 1900..currentYear) {
                return ValidationResult(false, "Năm sinh trong số CCCD không hợp lệ")
            }
        }

        return ValidationResult(true, "Số CCCD hợp lệ")
    }

}
