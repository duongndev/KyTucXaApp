package com.duongnd.kytucxa.data.remote.dto.auth.fcm

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FcmResponse(
    val user: UserFcmItem
)
