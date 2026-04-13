package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequiredDocuments(
    val cccdBack: Boolean = false,
    val cccdFront: Boolean = false,
    val priorityDoc: Boolean = false,
    val stampedForm: Boolean = false,
    val studentCard: Boolean = false
)
