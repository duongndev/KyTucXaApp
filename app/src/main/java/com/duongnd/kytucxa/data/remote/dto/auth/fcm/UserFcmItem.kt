package com.duongnd.kytucxa.data.remote.dto.auth.fcm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFcmItem(
    @Json(name = "_id")
    val id: String,
    val fullName: String,
    val gender: String,
    val email: String,
    val fcmToken: String
)