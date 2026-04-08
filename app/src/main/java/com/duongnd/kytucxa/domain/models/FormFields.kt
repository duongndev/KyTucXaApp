package com.duongnd.kytucxa.domain.models

import android.graphics.Bitmap

data class FormFields(
    val fullName: String,
    val gender: String,
    val dob: String,
    val idNumber: String,
    val idIssueDate: String,
    val idIssuePlace: String,
    val permanentAddress: String,
    val phoneNumber: String,
    val email: String,
    val emergencyContact: String,
    val schoolName: String,
    val academicYear: String,
    val className: String,
    val department: String,
    val studentId: String,
    val priorityType: String,
    val dormName: String,
    val duration: String,
    val signatureBitmap: Bitmap? = null
)
