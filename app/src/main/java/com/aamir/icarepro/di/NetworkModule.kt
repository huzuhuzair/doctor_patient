package com.aamir.icarepro.di

import com.aamir.icarepro.BuildConfig
import com.aamir.icarepro.data.AppDataManager
import com.aamir.icarepro.data.DataManager
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.network.ApiService
import com.aamir.icarepro.data.network.AuthenticationInterceptor
import com.aamir.icarepro.pushNotifications.MessagingService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }


    @Singleton
    @Provides
    fun providersLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun providedAuthenticationInterceptor(dataStoreHelper: DataStoreHelper): AuthenticationInterceptor {
        return AuthenticationInterceptor(
            dataStoreHelper
        )
    }
    @Provides
    @Singleton
    fun provideMessagingService(messagingService: MessagingService): MessagingService = MessagingService()

    @Singleton
    @Provides
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authenticationInterceptor: AuthenticationInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticationInterceptor)
            .readTimeout(240, TimeUnit.SECONDS)
            .connectTimeout(240, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppManager(
        mPreferencesHelper: DataStoreHelper,
        mApiHelper: ApiService
    ): DataManager {
        return AppDataManager(mPreferencesHelper, mApiHelper)
    }




}