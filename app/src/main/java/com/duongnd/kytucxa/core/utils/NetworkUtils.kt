package com.duongnd.kytucxa.core.utils

/**
 * Hàm tiện ích để thực thi một lời gọi API an toàn với try-catch.
 * @param apiCall Một lambda function chứa lời gọi API (suspend)
 * @return Result chứa dữ liệu trả về hoặc Exception
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
