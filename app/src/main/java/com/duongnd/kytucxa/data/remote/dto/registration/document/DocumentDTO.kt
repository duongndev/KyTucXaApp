package com.duongnd.kytucxa.data.remote.dto.registration.document

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentDTO(
    @Json(name = "_id")
    val id: String? = null,
    val registrationForm: String? = null,
    val type: String? = null,
    val fileUrl: String? = null,
    val publicId: String? = null,
    val signedUrl: String? = null,
    val note: String? = null,
    val uploadedBy: String? = null,
    val status: String? = null,
    val createdAt: String? =null,
    val updatedAt: String? = null,
)
