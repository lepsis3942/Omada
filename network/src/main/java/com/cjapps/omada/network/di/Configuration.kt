package com.cjapps.omada.network.di

import com.cjapps.omada.network.FlickrImageService
import com.cjapps.omada.network.IImageService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkConfiguration {
    companion object {
        @Provides
        @Singleton
        fun providesHttpClient(): HttpClient {
            return HttpClient(Android) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                        }
                    )
                }
            }
        }

        @Provides
        @Singleton
        fun providesCoroutineDispatcher(): CoroutineDispatcher {
            return Dispatchers.IO
        }
    }

    @Binds
    @Singleton
    abstract fun bindsFlickrService(
        flickrService: FlickrImageService
    ): IImageService
}