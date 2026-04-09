package com.duongnd.kytucxa.domain.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationResponse
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    suspend fun createRegistrationForm(registrationRequest: RegistrationRequest): Flow<Resource<RegistrationResponse>>
    suspend fun updateResidenceForm(id: String, residenceRequest: ResidenceRequest): Flow<Resource<ResidenceResponse>>
    suspend fun updateTemporaryForm(id: String, temporaryRequest: TemporaryRequest): Flow<Resource<TemporaryResponse>>
    suspend fun getCurrentDraft(): Flow<Resource<DraftResponse>>
}