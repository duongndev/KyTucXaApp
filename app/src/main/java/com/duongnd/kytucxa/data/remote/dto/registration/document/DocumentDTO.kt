package com.duongnd.kytucxa.data.remote.dto.registration.document

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentDTO(
    @Json(name = "_id")
    val id: String?,
    val registrationForm: String?,
    val type: String?,
    val fileUrl: String?,
    val note: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val status: String?,
    val uploadedBy: String?,
    val publicId: String?,
    val signedUrl: String?
)
