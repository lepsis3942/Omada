package com.cjapps.omada.network.di

import com.cjapps.omada.network.BuildConfig
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
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.http.path
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
                defaultRequest {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = "flickr.com"
                        path("services/rest")
                        parameters.append("api_key", BuildConfig.FLICKR_API_KEY)
                        parameters.append("format", "json")
                        parameters.append("nojsoncallback", "1")
                    }
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
    internal abstract fun bindsFlickrService(
        flickrService: FlickrImageService
    ): IImageService
}