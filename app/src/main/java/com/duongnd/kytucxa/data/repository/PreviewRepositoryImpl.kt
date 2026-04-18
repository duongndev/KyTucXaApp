package com.duongnd.kytucxa.data.repository

import com.duongnd.kytucxa.core.utils.Resource
import com.duongnd.kytucxa.data.remote.api.PreviewApi
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PreviewRepositoryImpl @Inject constructor(
    private val previewApi: PreviewApi
) : PreviewRepository {
    override suspend fun getPreviewTemporary(id: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = previewApi.getPreviewTemporaryApi(id)
                if (response.isSuccessful) {
                    val previewData = response.body() ?: ""
                    emit(Resource.Success(previewData))
                } else {
                    emit(Resource.Error("Failed to load preview: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("An error occurred: ${e.localizedMessage}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getPreviewResidence(id: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = previewApi.getPreviewResidenceApi(id)
                if (response.isSuccessful) {
                    val previewData = response.body() ?: ""
                    emit(Resource.Success(previewData))
                } else {
                    emit(Resource.Error("Failed to load preview: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("An error occurred: ${e.localizedMessage}"))
            }
        }.flowOn(Dispatchers.IO)
    }
}
