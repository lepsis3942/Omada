package com.cjapps.omada.network

import com.cjapps.omada.network.models.NetworkImage
import com.cjapps.omada.network.models.NetworkPaginated
import com.cjapps.omada.network.models.internal.FlickrPhotoResponse
import com.cjapps.omada.network.models.internal.toPaginatedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FlickrImageService @Inject constructor(
    private val httpClient: HttpClient,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IImageService {

    override suspend fun getRecentPhotos(
        page: Int,
        perPage: Int
    ): Result<NetworkPaginated<NetworkImage>> {
        try {
            val response = withContext(coroutineDispatcher) {
                httpClient.get {
                    parameter("method", "flickr.photos.getRecent")
                    parameter("page", page.toString())
                    parameter("per_page", perPage.toString())
                    parameter("extras", "description,date_upload")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                val result = response.body<FlickrPhotoResponse>()
                return Result.success(result.toPaginatedResponse())
            }
            // Could log contents from body or more details if not sensitive information in a real app
            return Result.failure(Exception("Error calling recent photos. Response: $response"))
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    override suspend fun getPhotosBySearch(
        searchText: String,
        page: Int,
        perPage: Int
    ): Result<NetworkPaginated<NetworkImage>> {
        try {
            val response = withContext(coroutineDispatcher) {
                httpClient.get {
                    parameter("method", "flickr.photos.search")
                    parameter("text", searchText)
                    parameter("page", page.toString())
                    parameter("per_page", perPage.toString())
                    parameter("extras", "description,date_upload")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                val result = response.body<FlickrPhotoResponse>()
                return Result.success(result.toPaginatedResponse())
            }
            // Could log contents from body or more details if not sensitive information in a real app
            return Result.failure(Exception("Error calling search photos. Response: $response"))
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }
}