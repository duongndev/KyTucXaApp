package com.duongnd.kytucxa.core.utils

sealed class Resource<out T> {
    object Idle : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T, val message: String? = null) : Resource<T>()
    data class Error(val message: String, val data: Any? = null) : Resource<Nothing>()
}
