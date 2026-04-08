package com.duongnd.kytucxa.core.di

import com.duongnd.kytucxa.BuildConfig
import com.duongnd.kytucxa.data.remote.api.AuthApi
import com.duongnd.kytucxa.data.remote.api.PreviewApi
import com.duongnd.kytucxa.data.remote.api.RegistrationApi
import com.duongnd.kytucxa.data.remote.interceptor.AuthInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = "http://10.0.2.2:6023/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String, moshi: Moshi, okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun providePreviewApi(retrofit: Retrofit): PreviewApi {
        return retrofit.create(PreviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRegistrationApi(retrofit: Retrofit): RegistrationApi {
        return retrofit.create(RegistrationApi::class.java)
    }

}
