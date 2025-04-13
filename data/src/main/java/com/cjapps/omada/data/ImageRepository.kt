package com.cjapps.omada.data

import com.cjapps.omada.data.exceptions.UnknownErrorException
import com.cjapps.omada.data.models.Image
import com.cjapps.omada.data.models.Paginated
import com.cjapps.omada.data.models.toImage
import com.cjapps.omada.data.models.toPaginated
import com.cjapps.omada.network.IImageService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ImageRepository @Inject constructor(
    private val imageService: IImageService,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IImageRepository {
    override suspend fun getRecentPhotos(
        page: Int,
        perPage: Int
    ): Result<Paginated<Image>> {
        return withContext(coroutineDispatcher) {
            val serviceResult = imageService.getRecentPhotos(page = page, perPage = perPage)
            val result = serviceResult.getOrNull()

            if (result == null) {
                return@withContext Result.failure(
                    serviceResult.exceptionOrNull() ?: UnknownErrorException()
                )
            }

            return@withContext try {
                Result.success(result.toPaginated { it.toImage() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun searchPhotos(
        searchText: String,
        page: Int,
        perPage: Int
    ): Result<Paginated<Image>> {
        return withContext(coroutineDispatcher) {
            val serviceResult =
                imageService.getPhotosBySearch(searchText, page = page, perPage = perPage)
            val result = serviceResult.getOrNull()

            if (result == null) {
                return@withContext Result.failure(
                    serviceResult.exceptionOrNull() ?: UnknownErrorException()
                )
            }

            return@withContext try {
                Result.success(result.toPaginated { it.toImage() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}