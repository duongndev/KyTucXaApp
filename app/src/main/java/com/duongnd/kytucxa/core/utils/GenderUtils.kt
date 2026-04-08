package com.duongnd.kytucxa.core.utils

object GenderUtils {

    fun getGenderDisplay(gender: String?): String {
        return when (gender) {
            "male" -> "Nam"
            "female" -> "Nữ"
            else -> "Khác"
        }
    }

    fun getGenderValue(display: String): String {
        return when (display) {
            "Nam" -> "male"
            "Nữ" -> "female"
            else -> {
                "Khác"
            }

        }
    }
}