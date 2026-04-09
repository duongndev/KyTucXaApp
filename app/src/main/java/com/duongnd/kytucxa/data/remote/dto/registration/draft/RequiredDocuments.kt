package com.duongnd.kytucxa.data.remote.dto.registration.draft

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequiredDocuments(
    val cccdBack: Boolean,
    val cccdFront: Boolean,
    val priorityDoc: Boolean,
    val stampedForm: Boolean,
    val studentCard: Boolean
)