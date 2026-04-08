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
    override suspend fun getPreviewTamTru(templateName: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = previewApi.getPreviewTamTru(templateName)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        emit(Resource.Success(body))
                    } else {
                        emit(Resource.Error("Dữ liệu rỗng"))
                    }
                } else {
                    emit(Resource.Error("Lỗi: ${response.code()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Mất kết nối"))
            }
        }.flowOn(Dispatchers.IO)
    }


}
