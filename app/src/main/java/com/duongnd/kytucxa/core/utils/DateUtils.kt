package com.duongnd.kytucxa.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    const val DEFAULT_DATE_FORMAT = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT = "HH:mm dd/MM/yyyy"
    const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /**
     * Format Date object to String
     */
    fun formatDate(date: Date?, pattern: String = DEFAULT_DATE_FORMAT): String {
        if (date == null) return ""
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Format String date from one pattern to another
     */
    fun formatString(
        dateString: String?,
        inputPattern: String = API_DATE_FORMAT,
        outputPattern: String = DEFAULT_DATE_FORMAT
    ): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val inputSdf = SimpleDateFormat(inputPattern, Locale.getDefault())
            if (inputPattern == API_DATE_FORMAT) {
                inputSdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = inputSdf.parse(dateString)
            val outputSdf = SimpleDateFormat(outputPattern, Locale.getDefault())
            date?.let { outputSdf.format(it) } ?: ""
        } catch (e: Exception) {
            dateString // Return original if error
        }
    }

    /**
     * Get current date as string
     */
    fun getCurrentDate(pattern: String = DEFAULT_DATE_FORMAT): String {
        return formatDate(Date(), pattern)
    }
}
