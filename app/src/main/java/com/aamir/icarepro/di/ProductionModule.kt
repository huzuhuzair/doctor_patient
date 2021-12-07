package com.aamir.icarepro.di

import android.content.Context
import com.aamir.icarepro.data.dataStore.DataStoreConstants
import com.aamir.icarepro.utils.ImageUtility
import com.aamir.icarepro.utils.PermissionFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ProductionModule {


    @Singleton
    @Provides
    fun provideString(): String {
        return DataStoreConstants.DataStoreName
    }

    @Singleton
    @Provides
    fun providePermissionFile(
        @ApplicationContext appContext: Context
    ): PermissionFile {
        return PermissionFile(appContext)
    }


    @Singleton
    @Provides
    fun provideImageUtility(
        @ApplicationContext appContext: Context
    ): ImageUtility {
        return ImageUtility(appContext)
    }

}
