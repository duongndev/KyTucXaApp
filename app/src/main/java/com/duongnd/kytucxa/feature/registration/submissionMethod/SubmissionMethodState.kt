package com.duongnd.kytucxa.feature.registration.submissionMethod

import com.duongnd.kytucxa.domain.models.SubmissionMethod

data class SubmissionMethodState(
    val selectedMethod: SubmissionMethod? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val requiredDocuments: List<String> = listOf(
        "Đơn xin nội trú (theo mẫu, có xác nhận của trường)",
        "02 Ảnh chân dung 3x4 (mới chụp)",
        "01 Bản photo CCCD (có công chứng)",
        "01 Bản photo Thẻ sinh viên/Giấy báo nhập học",
        "Giấy tờ ưu tiên (nếu có)"
    ),
    val downloadUrl: String = "https://drive.google.com/file/d/1GrRvz-EMlgvRblR6Wq7auHvrIoAN4ory/view?pli=1"
)
