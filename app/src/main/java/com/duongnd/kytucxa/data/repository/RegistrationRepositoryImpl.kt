package com.duongnd.kytucxa.data.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.core.utils.handleResponseResource
import com.duongnd.kytucxa.data.remote.api.RegistrationApi
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationResponse
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepositoryImpl @Inject constructor(
    private val registrationApi: RegistrationApi
) : RegistrationRepository {
    override suspend fun createRegistrationForm(registrationRequest: RegistrationRequest): Flow<Resource<RegistrationResponse>> {
        return handleResponseResource {
            registrationApi.createRegistrationFormApi(registrationRequest)
        }
    }

    override suspend fun updateResidenceForm(
        id: String,
        residenceRequest: ResidenceRequest
    ): Flow<Resource<ResidenceResponse>> {
        return handleResponseResource {
            registrationApi.updateResidenceFormApi(id, residenceRequest)
        }
    }

    override suspend fun updateTemporaryForm(
        id: String,
        temporaryRequest: TemporaryRequest
    ): Flow<Resource<TemporaryResponse>> {
        return handleResponseResource {
            registrationApi.updateTemporaryFormApi(id, temporaryRequest)
        }
    }

    override suspend fun getCurrentDraft(): Flow<Resource<DraftResponse>> {
        return handleResponseResource {
            registrationApi.getCurrentDraftApi()
        }
    }

    override suspend fun deleteRegistrationForm(id: String): Flow<Resource<Map<String, Any?>?>> {
        return handleResponseResource {
            registrationApi.deleteRegistrationFormApi(id)
        }
    }


}