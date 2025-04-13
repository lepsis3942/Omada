package com.cjapps.omada.network

import com.cjapps.omada.network.models.NetworkImage
import com.cjapps.omada.network.models.NetworkPaginated

interface IImageService {
    suspend fun getRecentPhotos(page: Int, perPage: Int): Result<NetworkPaginated<NetworkImage>>
    suspend fun getPhotosBySearch(
        searchText: String,
        page: Int,
        perPage: Int
    ): Result<NetworkPaginated<NetworkImage>>
}