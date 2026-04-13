package com.duongnd.kytucxa.core.utils

import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.Response

inline fun <T> handleResponseResource(
    crossinline apiCall: suspend () -> Response<ApiResponse<T>>
): Flow<Resource<T>> = flow<Resource<T>> {

    emit(Resource.Loading)

    try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()

            if (body == null) {
                emit(Resource.Error("Dữ liệu trả về rỗng"))
                return@flow
            }

            // Trường hợp success: true từ API
            if (body.success) {
                @Suppress("UNCHECKED_CAST")
                val data = body.data as T
                emit(Resource.Success(data, body.message))
            } else {
                emit(Resource.Error(body.message, body.data))
            }

        } else {
            val errorBody = response.errorBody()?.string()
            var message = "HTTP ${response.code()}"
            var errorData: Any? = null

            try {
                val json = JSONObject(errorBody ?: "")
                message = json.optString("message", message)
                
                if (json.has("data")) {
                    errorData = json.opt("data")
                }
            } catch (e: Exception) {
                // Ignore parsing error
            }

            emit(Resource.Error(message, errorData))
        }

    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Mất kết nối"))
    }

}.flowOn(Dispatchers.IO)
