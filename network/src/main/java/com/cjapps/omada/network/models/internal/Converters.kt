package com.cjapps.omada.network.models.internal

import com.cjapps.omada.network.models.NetworkImage
import com.cjapps.omada.network.models.Paginated

fun FlickrPhotoResponse.toPaginatedResponse(): Paginated<List<NetworkImage>> {
    return Paginated<List<NetworkImage>>(
        page = this.photos.page,
        pages = this.photos.pages,
        perPage = this.photos.perPage,
        total = this.photos.total,
        item = this.photos.photo.map { it.toNetworkImage() }
    )
}

fun FlickrImage.toNetworkImage(): NetworkImage {
    // This should be in a more configurable place but for the purposes of a demo app this should suffice
    return NetworkImage(
        imageUrl = "https://live.staticflickr.com/${this.server}/${this.id}_${this.secret}.jpg"
    )
}