package com.cjapps.omada.data.models

import com.cjapps.omada.network.models.NetworkImage
import com.cjapps.omada.network.models.NetworkPaginated
import java.util.Date

fun <T, R> NetworkPaginated<T>.toPaginated(itemConverter: (T) -> R): Paginated<R> {
    return Paginated(
        page = this.page,
        pages = this.pages,
        perPage = this.perPage,
        total = this.total,
        items = this.items.map { itemConverter(it) }
    )
}

fun NetworkImage.toImage(): Image {
    return Image(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        description = this.description,
        dateUpload = this.dateUpload?.let {
            Date(it)
        },
    )
}