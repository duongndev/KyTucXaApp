package com.duongnd.kytucxa.domain.repository

import com.duongnd.kytucxa.core.utils.Resource


import kotlinx.coroutines.flow.Flow

interface PreviewRepository {
    suspend fun getPreviewTamTru(templateName: String): Flow<Resource<String>>
}
