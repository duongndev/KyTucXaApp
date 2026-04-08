package com.duongnd.kytucxa.data.remote.api

import com.duongnd.kytucxa.data.remote.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PreviewApi {

    @GET("/api/preview/template/{templateName}/preview")
    suspend fun getPreviewTamTru(
        @Path("templateName") templateName: String,
    ): Response<String>

}
