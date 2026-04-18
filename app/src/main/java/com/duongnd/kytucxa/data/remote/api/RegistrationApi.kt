package com.duongnd.kytucxa.data.remote.api

import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateRequest
import com.duongnd.kytucxa.data.remote.dto.registration.create.RegistrationCreateResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.CurrentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.document.UploadDocumentResponse
import com.duongnd.kytucxa.data.remote.dto.registration.current.draft.DraftResponse
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceRequest
import com.duongnd.kytucxa.data.remote.dto.registration.residence.ResidenceResponse
import com.duongnd.kytucxa.data.remote.dto.registration.submit.RegistrationSubmitRequest
import com.duongnd.kytucxa.data.remote.dto.registration.submit.RegistrationSubmitResponse
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryRequest
import com.duongnd.kytucxa.data.remote.dto.registration.temporary.TemporaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RegistrationApi {

    @POST("/api/registrations")
    suspend fun createRegistrationFormApi(
        @Body createRegistration: RegistrationCreateRequest
    ): Response<ApiResponse<RegistrationCreateResponse>>

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
    suspend fun getCurrentRegistrationApi(): Response<ApiResponse<CurrentResponse>>

    @DELETE("/api/registrations/{id}")
    suspend fun deleteRegistrationFormApi(
        @Path("id") id: String
    ): Response<ApiResponse<Map<String, Any?>?>>


    @Multipart
    @POST("/api/registrations/{formId}/documents/sensitive")
    suspend fun updateDocumentFormApi(
        @Path("formId") formId: String,
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody,
        @Part("note") note: RequestBody? = null
    ): Response<ApiResponse<UploadDocumentResponse>>


    @POST("/api/registrations/{id}/submit")
    suspend fun submitRegistrationFormApi(
        @Path("id") id: String,
        @Body signature: RegistrationSubmitRequest
    ): Response<ApiResponse<RegistrationSubmitResponse>>

}