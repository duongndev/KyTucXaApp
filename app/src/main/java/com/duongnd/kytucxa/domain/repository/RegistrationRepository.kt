package com.duongnd.kytucxa.domain.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.document.UploadDocumentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.data.remote.dto.registration.submit.RegistrationSubmitResponse
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface RegistrationRepository {
    suspend fun createRegistrationForm(registrationCreateRequest: RegistrationCreateRequest): Flow<Resource<RegistrationCreateResponse>>
    suspend fun updateResidenceForm(
        id: String,
        residenceRequest: ResidenceRequest
    ): Flow<Resource<ResidenceResponse>>
    suspend fun updateTemporaryForm(
        id: String,
        temporaryRequest: TemporaryRequest
    ): Flow<Resource<TemporaryResponse>>

    suspend fun deleteRegistrationForm(id: String): Flow<Resource<Map<String, Any?>?>>
    suspend fun updateDocumentForm(
        formId: String,
        image: MultipartBody.Part,
        type: String,
        note: String?
    ): Flow<Resource<UploadDocumentResponse>>

    suspend fun submitRegistrationForm(
        id: String,
        signature: String
    ): Flow<Resource<RegistrationSubmitResponse>>

    suspend fun getCurrentRegistration(): Flow<Resource<CurrentResponse>>

    fun setCachedRegistration(response: CurrentResponse)
    fun getCachedRegistration(): CurrentResponse?
    fun clearCachedRegistration()
}