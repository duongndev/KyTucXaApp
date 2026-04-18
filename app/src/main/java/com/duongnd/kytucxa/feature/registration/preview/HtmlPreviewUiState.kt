package com.duongnd.kytucxa.feature.registration.preview

import com.duongnd.kytucxa.core.utils.Resource

/**
 * Trạng thái giao diện của màn hình xem trước HTML
 */
data class HtmlPreviewUiState(
    val isLoading: Boolean = false,
    val htmlContent: String? = null,
    val errorMessage: String? = null,
    val title: String = "Xem trước đơn"
) {
    val isSuccess: Boolean = htmlContent != null
    val isError: Boolean = errorMessage != null
}
