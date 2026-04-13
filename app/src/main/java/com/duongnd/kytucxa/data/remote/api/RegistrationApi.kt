package com.duongnd.kytucxa.data.remote.api

import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationResponse
import com.duongnd.kytucxa.data.remote.dto.registration.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RegistrationApi {

    @POST("/api/registrations")
    suspend fun createRegistrationFormApi(
         @Body createRegistration: RegistrationRequest
    ): Response<ApiResponse<RegistrationResponse>>

    @PATCH("/api/registrations/{id}/step1")
    suspend fun updateResidenceFormApi(
        @Path("id") id: String,
        @Body residenceRequest: ResidenceRequest
    ): Response<ApiResponse<ResidenceResponse>>

    @PATCH("/api/registrations/{id}/step2")
    suspend fun updateTemporaryFormApi(
        @Path("id") id: String,
        @Body temporaryRequest: TemporaryRequest
    ): Response<ApiResponse<TemporaryResponse>>

    @GET("/api/registrations/my-current")
    suspend fun getCurrentDraftApi(): Response<ApiResponse<DraftResponse>>

    @DELETE("/api/registrations/{id}")
    suspend fun deleteRegistrationFormApi(
        @Path("id") id: String
    ): Response<ApiResponse<Map<String, Any?>?>>
}