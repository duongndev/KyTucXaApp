package com.duongnd.kytucxa.domain.repository

import com.duongnd.kytucxa.core.utils.Resource


import kotlinx.coroutines.flow.Flow

interface PreviewRepository {
    suspend fun getPreviewTemporary(id: String): Flow<Resource<String>>
    suspend fun getPreviewResidence(id: String): Flow<Resource<String>>
}
