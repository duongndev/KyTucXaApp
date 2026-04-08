package com.duongnd.kytucxa.core.di

import com.duongnd.kytucxa.data.repository.AuthRepositoryImpl
import com.duongnd.kytucxa.data.repository.PreviewRepositoryImpl
import com.duongnd.kytucxa.data.repository.RegistrationRepositoryImpl
import com.duongnd.kytucxa.domain.repository.AuthRepository
import com.duongnd.kytucxa.domain.repository.PreviewRepository
import com.duongnd.kytucxa.domain.repository.RegistrationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPreviewRepository(
        previewRepositoryImpl: PreviewRepositoryImpl
    ): PreviewRepository

    @Binds
    @Singleton
    abstract fun bindRegistrationRepository(
        registrationRepositoryImpl: RegistrationRepositoryImpl
    ): RegistrationRepository

}
