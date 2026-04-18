package com.duongnd.kytucxa.data.remote.dto.registration

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequiredDocumentsDTO(
    val cccdBack: Boolean = false,
    val cccdFront: Boolean = false,
    val priorityDoc: Boolean = false,
    val stampedForm: Boolean = false,
    val studentCard: Boolean = false,
    val photo3x4: Boolean = false
)
