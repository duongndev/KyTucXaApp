package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentDraft(
    val _id: String,
    val createdAt: String,
    val fileUrl: String,
    val note: String,
    val registrationForm: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val uploadedBy: String
)