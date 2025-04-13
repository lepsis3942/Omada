package com.cjapps.omada.data

import com.cjapps.omada.data.models.Image
import com.cjapps.omada.data.models.Paginated

interface IImageRepository {
    suspend fun getRecentPhotos(page: Int, perPage: Int): Result<Paginated<Image>>
    suspend fun searchPhotos(searchText: String, page: Int, perPage: Int): Result<Paginated<Image>>
}