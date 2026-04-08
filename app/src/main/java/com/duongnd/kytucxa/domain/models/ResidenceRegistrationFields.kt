package com.duongnd.kytucxa.domain.models

import android.graphics.Bitmap

data class ResidenceRegistrationFields(
    val receiver: String,
    val fullName: String,
    val dob: String,
    val gender: String,
    val idNumber: String,
    val phoneNumber: String,
    val email: String,
    val ownerName: String,
    val ownerRelation: String,
    val ownerIdNumber: String,
    val requestContent: String,
    val signatureBitmap: Bitmap? = null
)
