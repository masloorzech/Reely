package com.example.reely.di

import com.example.reely.data.remote.tmdb.TmdbApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import com.example.reely.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader(
                        name = "Authorization",
                        value = "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}"
                    )
                    .addHeader(
                        name = "accept",
                        value = "application/json"
                    )
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideTmdbApi(
        retrofit: Retrofit
    ): TmdbApi {
        return retrofit.create(TmdbApi::class.java)
    }
}