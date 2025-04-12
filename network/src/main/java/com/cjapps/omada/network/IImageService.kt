package com.cjapps.omada.network

import com.cjapps.omada.network.models.NetworkImage

interface IImageService {
    suspend fun getRecentPhotos(page: Int, perPage: Int): Result<List<NetworkImage>>
    suspend fun getPhotosBySearch(
        searchText: String,
        page: Int,
        perPage: Int
    ): Result<List<NetworkImage>>
}