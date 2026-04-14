package com.duongnd.kytucxa.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PreviewApi {

    @GET("/api/registrations/preview-temporary/{id}")
    suspend fun getPreviewTemporaryApi(
        @Path("id") id: String,
    ): Response<String>

    @GET("/api/registrations/preview-residence/{id}")
    suspend fun getPreviewResidenceApi(
        @Path("id") id: String,
    ): Response<String>

}
