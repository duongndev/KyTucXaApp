package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentDraft(
    @Json(name = "_id")
    val id: String,
    val createdAt: String,
    val fileUrl: String,
    val note: String,
    val registrationForm: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val uploadedBy: String
)