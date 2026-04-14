package com.duongnd.kytucxa.data.remote.dto.registration.document

import com.duongnd.kytucxa.data.remote.dto.registration.draft.RequiredDocuments
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadDocumentResponse(
    val document: DocumentDTO? = null,
    val canSubmit: Boolean? = false,
    val missingDocs: List<String>? = emptyList(),
    val requiredDocuments: RequiredDocuments?,
    val currentStep: Int? = 0,
)