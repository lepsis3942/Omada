package com.cjapps.omada.data.di

import com.cjapps.omada.data.IImageRepository
import com.cjapps.omada.data.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryConfiguration {

    @Binds
    internal abstract fun bindsPhotoRepository(
        photoRepository: ImageRepository
    ): IImageRepository
}