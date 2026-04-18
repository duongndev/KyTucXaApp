package com.duongnd.kytucxa.feature.registration.uploadDocument

import android.net.Uri
import com.duongnd.kytucxa.data.remote.dto.registration.document.UploadDocumentResponse
import com.duongnd.kytucxa.core.utils.Resource

/**
 * Trạng thái của màn hình upload giấy tờ.
 * Quản lý các ảnh đã chọn và kết quả từ API.
 */
data class UploadDocumentState(
    // Dữ liệu ảnh được chọn từ Gallery/Camera
    val idCardFront: Uri? = null,
    val idCardBack: Uri? = null,
    val studentCard: Uri? = null,
    val priorityDoc: Uri? = null,

    // Trạng thái gọi API upload (Dùng Resource.Idle làm mặc định)
    val uploadResult: Resource<UploadDocumentResponse> = Resource.Idle,

    // Tên trường đang upload (ID_CARD_FRONT, ID_CARD_BACK, v.v.)
    val uploadingField: String? = null,
    
    // Flag báo hiệu đã upload xong tất cả (để chuyển màn hình)
    val isAllUploadSuccess: Boolean = false
) {
    /**
     * Kiểm tra xem form đã hợp lệ chưa (3 ảnh bắt buộc phải có).
     * Dùng để enable/disable nút "Tiếp tục" trên UI.
     */
    val canUploadSuccess: Boolean
        get() = idCardFront != null && 
                idCardBack != null && 
                studentCard != null

    /**
     * Kiểm tra xem có đang trong quá trình upload không.
     */
    val isLoading: Boolean
        get() = uploadResult is Resource.Loading
}
