package com.aamir.icarepro.di

import android.content.Context
import com.aamir.icarepro.data.dataStore.AppDataStoreImpl
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providedDataStore(
        someString: String,
        @ApplicationContext appContext: Context,
        gson: Gson
    ): DataStoreHelper {
        return AppDataStoreImpl(appContext,someString,gson)
    }


}