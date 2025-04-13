package com.cjapps.omada.network.models.internal

import com.cjapps.omada.network.models.NetworkImage
import com.cjapps.omada.network.models.NetworkPaginated

internal fun FlickrPhotoResponse.toPaginatedResponse(): NetworkPaginated<NetworkImage> {
    return NetworkPaginated<NetworkImage>(
        page = this.photos.page,
        pages = this.photos.pages,
        perPage = this.photos.perPage,
        total = this.photos.total,
        items = this.photos.photo.map { it.toNetworkImage() }
    )
}

internal fun FlickrImage.toNetworkImage(): NetworkImage {
    // This should be in a more configurable place but for the purposes of a demo app this should suffice
    return NetworkImage(
        id = this.id,
        imageUrl = "https://live.staticflickr.com/${this.server}/${this.id}_${this.secret}.jpg"
    )
}