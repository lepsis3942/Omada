package com.cjapps.omada.network.models.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlickrPhotoResponse(val photos: FlickrPhotoPagedResponse, val stat: String)

@Serializable
data class FlickrPhotoPagedResponse(
    val page: Int,
    val pages: Int,
    @SerialName("perpage")
    val perPage: Int,
    val total: Int,
    val photo: List<FlickrImage>
)